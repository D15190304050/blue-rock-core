package bluerock.service;

import bluerock.api.IFileIOService;
import bluerock.dao.FileMetadataMapper;
import bluerock.dao.FileChunkMapper;
import bluerock.dao.FileUploadingTaskMapper;
import bluerock.dao.UserFileMapper;
import bluerock.domain.FileMetadata;
import bluerock.domain.FileUploadingTask;
import bluerock.domain.UserFile;
import bluerock.minio.Bucket;
import bluerock.minio.EasyMinio;
import bluerock.minio.FileUploadingMonitor;
import bluerock.minio.IProgressListener;
import bluerock.params.BatchMultipartFileParam;
import bluerock.params.CanUploadFileParam;
import bluerock.params.MultipartFileParam;
import dataworks.ExceptionInfoFormatter;
import dataworks.autoconfig.web.LogArgumentsAndResponse;
import dataworks.web.commons.ServiceResponse;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@LogArgumentsAndResponse
public class FileIOService implements IFileIOService
{
    @Autowired
    private EasyMinio easyMinio;

    @Autowired
    private UserFileMapper userFileMapper;

    @Autowired
    private FileMetadataMapper fileMetadataMapper;

    @Autowired
    private FileChunkMapper fileChunkMapper;

    @Autowired
    private FileUploadingTaskMapper fileUploadingTaskMapper;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private ValueOperations<String, String> valueOperations;

    /**
     * Call this method before executing real uploading operation.
     * @param canUploadFileParam
     * @return
     */
    public ServiceResponse<Boolean> canUploadFile(@Validated CanUploadFileParam canUploadFileParam)
    {
        int fileCount = fileMetadataMapper.countFileByCondition(canUploadFileParam);
        if (fileCount == 0)
            return ServiceResponse.buildSuccessResponse(true);
        else
            return ServiceResponse.buildSuccessResponse(false, "File exists.");
    }

    @Override
    @Transactional
    public ServiceResponse<String> uploadFile(@Validated MultipartFileParam multipartFileParam)
    {
        MultipartFile file = multipartFileParam.getFile();
        long parentDirectoryId = multipartFileParam.getParentDirectoryId();
        long userId = multipartFileParam.getUserId();

        String fileName = file.getName();

        // Save metadata to database (file_metadata, user_file).
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setUserId(userId);
        fileMetadata.setFileName(fileName);
        fileMetadata.setDirectoryId(parentDirectoryId);
        fileMetadata.setBucketName(Bucket.getBucketName(userId));
        fileMetadata.setObjectName(fileMetadataService.generateUserFilePath(userId, parentDirectoryId, fileName));
        fileMetadataMapper.insertNew(fileMetadata);

        long fileId = fileMetadata.getId();
        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        userFile.setFileId(fileId);
        userFileMapper.insert(userFile);

        // Upload data by upload task manager.
        // 1) Create the uploading task.
        // 2) Keep monitoring the uploading progress.

        IProgressListener progressListener = progress ->
        {
            // Send progress to frontend through websocket.
            ;
        };

        try
        {
            FileUploadingMonitor fileUploadingMonitor = new FileUploadingMonitor(easyMinio, valueOperations, fileMetadata, progressListener, fileChunkMapper, fileUploadingTaskMapper);
            Thread fileUploadingThread = fileUploadingMonitor.start(file);
            FileUploadingTask fileUploadingTask = fileUploadingMonitor.getFileUploadingTask();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ServiceResponse<Boolean> uploadFiles(BatchMultipartFileParam batchMultipartFileParam)
    {
        return null;
    }

    @Override
    public boolean deleteFiles(List<FileMetadata> fileMetadataList)
    {
        FileMetadata firstElement = fileMetadataList.get(0);
        long userId = firstElement.getUserId();
        String bucketName = firstElement.getBucketName();

        // Delete records of the files in the database.
        List<Long> fileIds = fileMetadataList.stream().map(FileMetadata::getId).collect(Collectors.toList());
        fileMetadataMapper.deleteFilesByIds(userId, fileIds);

        List<String> objectNames = fileMetadataList.stream().map(FileMetadata::getObjectName).collect(Collectors.toList());

        try
        {
            // TODO: Make this operation async.
            return easyMinio.deleteObjects(bucketName, objectNames);
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e)
        {
            String exceptionStackTrace = ExceptionInfoFormatter.formatMessageAndStackTrace(e);
            log.error("Error when trying to delete files in MinIO..., " + exceptionStackTrace);
            return false;
        }
    }
}
