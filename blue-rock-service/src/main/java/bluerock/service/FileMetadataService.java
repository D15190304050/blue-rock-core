package bluerock.service;

import bluerock.api.IFileMetadataService;
import bluerock.dao.FileMetadataMapper;
import bluerock.domain.FileMetadata;
import bluerock.params.MoveFilesParam;
import bluerock.params.RenameParam;
import bluerock.params.ShowDirectoryAndFileParam;
import dataworks.params.ArgumentValidator;
import dataworks.web.commons.ServiceResponse;
import dataworks.web.commons.authorization.AuthorizationErrorMessageGenerator;
import dataworks.web.commons.authorization.CommonResourceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
public class FileMetadataService implements IFileMetadataService
{
    @Autowired
    private FileMetadataMapper fileMetadataMapper;

    @Lazy
    @Autowired
    private UserDirectoryService userDirectoryService;

    @Override
    public ServiceResponse<List<FileMetadata>> showFiles(ShowDirectoryAndFileParam showFileParam)
    {
        List<FileMetadata> fileMetadataList = getFileMetadataListByConditions(showFileParam);
        return ServiceResponse.buildSuccessResponse(fileMetadataList);
    }

    @Override
    public List<FileMetadata> getFileMetadataListByConditions(ShowDirectoryAndFileParam showFileParam)
    {
        ArgumentValidator.requireNonNull(showFileParam, "showFileParam");
        showFileParam.prepareForFile();

        return fileMetadataMapper.showFilesByConditions(showFileParam);
    }

    @Override
    public String getFilePathByFileId(long userId, long id)
    {
        FileMetadata fileMetadata = fileMetadataMapper.getFileMetadataByIdAndUserId(userId, id);
        long directoryId = fileMetadata.getDirectoryId();

        String fileName = fileMetadata.getFileName();

        String directoryPath = userDirectoryService.getDirectoryPathById(directoryId);
        return directoryPath + fileName;
    }

    @Override
    public String generateUserFilePath(long userId, long parentDirectoryId, String fileName)
    {
        String directoryPath = userDirectoryService.getDirectoryPathById(parentDirectoryId);
        return userId + "-" + directoryPath + fileName;
    }

    @Override
    public List<FileMetadata> getFileIdsInDirectoryIds(long userId, List<Long> directoryIds)
    {
        return fileMetadataMapper.getFilesInDirectories(userId, directoryIds);
    }

    @Override
    public ServiceResponse<FileMetadata> getFileMetadataById(long userId, long id)
    {
        FileMetadata fileMetadata = fileMetadataMapper.getFileMetadataByIdAndUserId(userId, id);
        return ServiceResponse.buildSuccessResponse(fileMetadata);
    }

    @Override
    public ServiceResponse<Boolean> renameFile(@Validated RenameParam renameParam)
    {
        int directoryCount = fileMetadataMapper.countFileByIdAndUserId(renameParam.getId(), renameParam.getUserId());
        if (directoryCount > 0)
        {
            fileMetadataMapper.updateNameById(renameParam);
            return ServiceResponse.buildSuccessResponse(true);
        }
        else
            return new ServiceResponse<>(-3, false, false, "File does not exists", null);
    }

    @Override
    public ServiceResponse<Boolean> moveFiles(@Validated MoveFilesParam moveFilesParam)
    {
        // Steps:
        // 1. Validate userId-fileId pairs, report resource authorization exception if there is any userId-fileId not match.
        // 2. Validate if the source directory (with ID = sourceDirectoryId) belongs to the user, report resource authorization exception if not.
        // 3. Validate if the destination directory (with ID = destinationDirectoryId) belongs to the user, report resource authorization exception if not.
        // 4. Validate if all these files are in the same directory, whose ID matches moveFilesParam.sourceDirectoryId.
        // 5. Set directoryId to destinationDirectoryId for all the files.

        long userId = moveFilesParam.getUserId();
        List<Long> fileIds = moveFilesParam.getFileIds();
        long sourceDirectoryId = moveFilesParam.getSourceDirectoryId();
        long destinationDirectoryId = moveFilesParam.getDestinationDirectoryId();

        List<Long> fileIdsNotBelongToUser = fileMetadataMapper.getFileIdsNotBelongToUser(userId, fileIds);
        if (!CollectionUtils.isEmpty(fileIdsNotBelongToUser))
            return ServiceResponse.buildErrorResponse(-8, AuthorizationErrorMessageGenerator.resourceNotPermitted(userId, fileIds, CommonResourceTypes.FILE));

        return null;
    }
}
