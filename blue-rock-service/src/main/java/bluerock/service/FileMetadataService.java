package bluerock.service;

import bluerock.api.IFileMetadataService;
import bluerock.api.IUserDirectoryService;
import bluerock.dao.FileMetadataMapper;
import bluerock.dao.UserDirectoryMapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
public class FileMetadataService implements IFileMetadataService
{
    @Autowired
    private FileMetadataMapper fileMetadataMapper;

    @Autowired
    private UserDirectoryMapper userDirectoryMapper;

    @Lazy
    @Autowired
    private IUserDirectoryService userDirectoryService;

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
    @Transactional
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
    @Transactional
    public ServiceResponse<Boolean> moveFiles(@Validated MoveFilesParam moveFilesParam)
    {
        // Steps:
        // 1. Validate if moveFilesParam.sourceDirectoryId = moveFilesParam.destinationDirectoryId, if they are equal, report exception.
        // 2. Validate userId-fileId pairs, report resource authorization exception if there is any userId-fileId not match.
        // 3. Validate if the source directory (with ID = sourceDirectoryId) belongs to the user, report resource authorization exception if not.
        // 4. Validate if the destination directory (with ID = destinationDirectoryId) belongs to the user, report resource authorization exception if not.
        // 5. Validate if all these files are in the same directory, whose ID matches moveFilesParam.sourceDirectoryId.
        // 6. Set directoryId to destinationDirectoryId for all the files.

        long userId = moveFilesParam.getUserId();
        List<Long> fileIds = moveFilesParam.getFileIds();
        long sourceDirectoryId = moveFilesParam.getSourceDirectoryId();
        long destinationDirectoryId = moveFilesParam.getDestinationDirectoryId();

        // Step 1.
        if (sourceDirectoryId == destinationDirectoryId)
            return ServiceResponse.buildErrorResponse(-12, "Source directory and destination directory must be different.");

        // Step 2.
        List<Long> fileIdsNotBelongToUser = fileMetadataMapper.getFileIdsNotBelongToUser(userId, fileIds);
        if (!CollectionUtils.isEmpty(fileIdsNotBelongToUser))
            return ServiceResponse.buildErrorResponse(-8, AuthorizationErrorMessageGenerator.resourceNotPermitted(userId, fileIds, CommonResourceTypes.FILE));

        // Step 3.
        int sourceDirectoryCount = userDirectoryMapper.countDirectoryByIdAndUserId(sourceDirectoryId, userId);
        if (sourceDirectoryCount == 0)
            return ServiceResponse.buildErrorResponse(-9, AuthorizationErrorMessageGenerator.resourceNotPermitted(userId, List.of(sourceDirectoryId), CommonResourceTypes.DIRECTORY));

        // Step 4.
        int destinationDirectoryCount = userDirectoryMapper.countDirectoryByIdAndUserId(destinationDirectoryId, userId);
        if (destinationDirectoryCount == 0)
            return ServiceResponse.buildErrorResponse(-9, AuthorizationErrorMessageGenerator.resourceNotPermitted(userId, List.of(destinationDirectoryId), CommonResourceTypes.DIRECTORY));

        // Step 5.
        List<Long> fileIdsNotInSourceDirectory = fileMetadataMapper.getFileIdsNotInDirectory(sourceDirectoryId, fileIds);
        if (!CollectionUtils.isEmpty(fileIdsNotInSourceDirectory))
            return ServiceResponse.buildErrorResponse(-10, String.format("Files with ID (%s) is (are) not in the directory (with ID: %d)", StringUtils.collectionToCommaDelimitedString(fileIdsNotInSourceDirectory), sourceDirectoryId));

        // Step 6.
        int updateCount = fileMetadataMapper.batchSetDirectoryId(destinationDirectoryId, fileIds);

        if (updateCount == fileIds.size())
            return ServiceResponse.buildSuccessResponse(true);
        else
            return ServiceResponse.buildErrorResponse(-11, "Internal error, see logs for more information.");
    }
}
