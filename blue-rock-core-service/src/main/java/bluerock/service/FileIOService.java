package bluerock.service;

import bluerock.api.IFileIOService;
import bluerock.dao.FileChunkMapper;
import bluerock.dao.FileMetadataMapper;
import bluerock.dao.FileUploadingTaskMapper;
import bluerock.dao.UserFileMapper;
import bluerock.domain.FileChunk;
import bluerock.domain.FileMetadata;
import bluerock.domain.FileUploadingTask;
import bluerock.minio.Bucket;
import bluerock.model.StorageObject;
import bluerock.params.*;
import bluerock.results.FileUploadingState;
import bluerock.states.FileChunkState;
import bluerock.states.FileMetadataState;
import dataworks.ExceptionInfoFormatter;
import dataworks.autoconfig.minio.EasyMinio;
import dataworks.autoconfig.web.LogArgumentsAndResponse;
import dataworks.web.commons.ServiceResponse;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@LogArgumentsAndResponse
public class FileIOService implements IFileIOService
{
    @Value("${slice-byte-count}")
    private long sliceByteCount;

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


        return null;
    }

    @Override
    @Transactional
    public ServiceResponse<Long> initFileUploadingTask(@Validated InitFileUploadingTaskParam initFileUploadingTaskParam)
    {
        long userId = initFileUploadingTaskParam.getUserId();
        long directoryId = initFileUploadingTaskParam.getDirectoryId();
        String fileName = initFileUploadingTaskParam.getFileName();
        long chunkCount = initFileUploadingTaskParam.getChunkCount();

        String bucketName = Bucket.getBucketName(userId);

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setUserId(userId);
        fileMetadata.setDirectoryId(directoryId);
        fileMetadata.setFileName(fileName);
        fileMetadata.setObjectName(StorageObject.randomObjectName());
        fileMetadata.setBucketName(bucketName);
        fileMetadata.setState(FileMetadataState.UPLOADING_UNFINISHED.getCode());
        fileMetadataMapper.insert(fileMetadata);

        FileUploadingTask fileUploadingTask = new FileUploadingTask();
        fileUploadingTask.setMetadataId(fileMetadata.getId());
        fileUploadingTask.setChunkCount(chunkCount);
        fileUploadingTask.setUserId(userId);
        fileUploadingTaskMapper.insert(fileUploadingTask);

        long fileUploadingTaskId = fileUploadingTask.getId();
        return ServiceResponse.buildSuccessResponse(fileUploadingTaskId);
    }

    @Override
    @Transactional
    public ServiceResponse<Boolean> uploadFileChunk(@Validated UploadFileChunkParam uploadFileChunkParam)
    {
        // Steps:
        // 1. Validate if the taskId-userId matches.
        // 2. Return true if the chunk is uploaded.
        // 3. Save the chunk.
        //  3.1 Save the chunk info into DB.
        //  3.2 Upload the chunk to MinIO.
        //  3.3 Set chunk uploading state in DB.
        //  3.4 Upload uploaded bytes of the task.

        long userId = uploadFileChunkParam.getUserId();
        long taskId = uploadFileChunkParam.getTaskId();
        long sliceIndex = uploadFileChunkParam.getSliceIndex();
        MultipartFile chunk = uploadFileChunkParam.getChunk();

        FileUploadingTask fileUploadingTask = fileUploadingTaskMapper.getTaskById(taskId);
        if (fileUploadingTask.getUserId() != userId)
            return ServiceResponse.buildErrorResponse(-9, "Error taskId-userId pair.");

        long chunkCount = fileUploadingTask.getChunkCount();

        if (sliceIndex >= chunkCount)
            return ServiceResponse.buildErrorResponse(-12, "Error slice index.");

        int chunkState = fileChunkMapper.getChunkStateByTaskIdAndSliceIndex(taskId, sliceIndex);
        if (chunkState == FileChunkState.FINISHED)
            return ServiceResponse.buildSuccessResponse(true);

        InputStream inputStream;
        long byteCount;
        try
        {
            inputStream = chunk.getInputStream();
            byteCount = inputStream.available();
        }
        catch (IOException e)
        {
            String errorMessage = ExceptionInfoFormatter.formatMessageAndStackTrace(e);
            log.error("Error when trying to get file input stream: " + errorMessage);
            return ServiceResponse.buildErrorResponse(-10, errorMessage);
        }

        if (byteCount <= 0)
            return ServiceResponse.buildErrorResponse(-13, "Nothing to read.");

        FileChunk fileChunk = new FileChunk();
        fileChunk.setTaskId(taskId);
        fileChunk.setByteCount(byteCount);
        fileChunk.setObjectName(StorageObject.randomObjectName());
        fileChunk.setSliceIndex(sliceIndex);
        fileChunkMapper.insert(fileChunk);

        String bucketName = Bucket.getBucketName(userId);
        try
        {
            easyMinio.uploadFile(chunk, bucketName);
        }
        catch (IOException | ServerException | InsufficientDataException | ErrorResponseException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e)
        {
            String errorMessage = ExceptionInfoFormatter.formatMessageAndStackTrace(e);
            log.error("Error when uploading the file chunk: " + errorMessage);
            return ServiceResponse.buildErrorResponse(-11, errorMessage);
        }

        fileChunkMapper.setChunkUploaded(fileChunk.getId());

        return ServiceResponse.buildSuccessResponse(true);
    }

    @Override
    @Transactional
    public ServiceResponse<Boolean> mergeChunks(@Validated MergeChunksParam mergeChunksParam)
    {
        // Steps:
        // 1. Validate if taskId-userId matches.
        // 2. Check if all chunks are uploaded.
        // 3. Merge them.
        // 4. Get the URL of the object, and save it to metadata.
        // 5. Set task state to finished.

        long taskId = mergeChunksParam.getTaskId();
        long userId = mergeChunksParam.getUserId();

        FileUploadingTask fileUploadingTask = fileUploadingTaskMapper.getTaskById(taskId);
        if (fileUploadingTask.getUserId() != userId)
            return ServiceResponse.buildErrorResponse(-9, "Error taskId-userId pair.");

        List<FileChunk> chunks = fileChunkMapper.getChunksByTaskId(taskId);
        if ((chunks.size() < fileUploadingTask.getChunkCount()) || (!FileChunk.allFinished(chunks)))
            return ServiceResponse.buildErrorResponse(-14, "File chunk uploading not finished.");

        List<String> chunkNames = chunks.stream().map(FileChunk::getObjectName).toList();

        long fileMetadataId = fileUploadingTask.getMetadataId();
        FileMetadata fileMetadata = fileMetadataMapper.getFileMetadataByIdAndUserId(userId, fileMetadataId);
        String bucketName = Bucket.getBucketName(userId);
        String objectName = fileMetadata.getObjectName();
        String url;
        try
        {
            easyMinio.composeObjects(bucketName, objectName, chunkNames);
            url = easyMinio.getObjectUrl(bucketName, objectName);
        }
        catch (IOException | ServerException | InsufficientDataException | ErrorResponseException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e)
        {
            String errorMessage = ExceptionInfoFormatter.formatMessageAndStackTrace(e);
            log.error("Error when composing file: " + errorMessage);
            return ServiceResponse.buildErrorResponse(-15, errorMessage);
        }

        if (url == null)
        {
            String errorMessage = "Error when trying to get file url.";
            log.error(errorMessage);
            return ServiceResponse.buildErrorResponse(-16, errorMessage);
        }

        fileMetadataMapper.setUrlById(fileMetadataId, url);

        return ServiceResponse.buildSuccessResponse(true);
    }

    @Override
    public ServiceResponse<FileUploadingState> getChunkStates(@Validated GetChunkStatesParam getChunkStatesParam)
    {
        long taskId = getChunkStatesParam.getTaskId();
        long userId = getChunkStatesParam.getUserId();

        FileUploadingTask fileUploadingTask = fileUploadingTaskMapper.getTaskById(taskId);
        if (fileUploadingTask.getUserId() != userId)
            return ServiceResponse.buildErrorResponse(-9, "Error taskId-userId pair.");

        List<bluerock.results.FileChunkState> chunkStatesByTaskId = fileChunkMapper.getChunkStatesByTaskId(taskId);
        FileUploadingState result = new FileUploadingState(taskId, chunkStatesByTaskId);

        return ServiceResponse.buildSuccessResponse(result);
    }

    @Override
    public ServiceResponse<Long> getSliceByteCount()
    {
        return ServiceResponse.buildSuccessResponse(sliceByteCount);
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
