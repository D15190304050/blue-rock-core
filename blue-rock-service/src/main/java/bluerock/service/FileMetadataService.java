package bluerock.service;

import bluerock.api.IFileMetadataService;
import bluerock.api.IUserDirectoryService;
import bluerock.dao.FileMetadataMapper;
import bluerock.dao.UserDirectoryMapper;
import bluerock.domain.FileMetadata;
import bluerock.params.*;
import dataworks.params.ArgumentValidator;
import dataworks.params.ValidationChain;
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

    public String validateSourceDestinationDirectoryIds(MoveFilesParam param)
    {
        if (param.getSourceDirectoryId() == param.getDestinationDirectoryId())
            return "Source directory and destination directory must be different.";
        else
            return null;
    }

    private <T extends IUserFileSourceDirectoryId> String validateUserFiles(T param)
    {
        long userId = param.getUserId();
        List<Long> fileIds = param.getFileIds();
        List<Long> fileIdsNotBelongToUser = fileMetadataMapper.getFileIdsNotBelongToUser(userId, fileIds);
        if (!CollectionUtils.isEmpty(fileIdsNotBelongToUser))
            return AuthorizationErrorMessageGenerator.resourceNotPermitted(userId, fileIds, CommonResourceTypes.FILE);
        else
            return null;
    }

    private <T extends IUserFileSourceDirectoryId> String validateSourceDirectoryBelongsToUser(T param)
    {
        long userId = param.getUserId();
        long sourceDirectoryId = param.getSourceDirectoryId();

        int sourceDirectoryCount = userDirectoryMapper.countDirectoryByIdAndUserId(sourceDirectoryId, userId);
        if (sourceDirectoryCount == 0)
            return AuthorizationErrorMessageGenerator.resourceNotPermitted(userId, List.of(sourceDirectoryId), CommonResourceTypes.DIRECTORY);
        else
            return null;
    }

    private String validateDestinationDirectoryBelongsToUser(MoveFilesParam param)
    {
        long userId = param.getUserId();
        long destinationDirectoryId = param.getDestinationDirectoryId();

        int sourceDirectoryCount = userDirectoryMapper.countDirectoryByIdAndUserId(destinationDirectoryId, userId);
        if (sourceDirectoryCount == 0)
            return AuthorizationErrorMessageGenerator.resourceNotPermitted(userId, List.of(destinationDirectoryId), CommonResourceTypes.DIRECTORY);
        else
            return null;
    }

    private <T extends IUserFileSourceDirectoryId> String validateFilesInSameDirectory(T param)
    {
        long sourceDirectoryId = param.getSourceDirectoryId();
        List<Long> fileIdsNotInSourceDirectory = fileMetadataMapper.getFileIdsNotInDirectory(sourceDirectoryId, param.getFileIds());
        if (!CollectionUtils.isEmpty(fileIdsNotInSourceDirectory))
            return String.format("Files with ID (%s) is (are) not in the directory (with ID: %d)", StringUtils.collectionToCommaDelimitedString(fileIdsNotInSourceDirectory), sourceDirectoryId);
        else
            return null;
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

        List<Long> fileIds = moveFilesParam.getFileIds();
        long destinationDirectoryId = moveFilesParam.getDestinationDirectoryId();

        ValidationChain<MoveFilesParam> validationChain = new ValidationChain<>(moveFilesParam);

        // Step 1.
        validationChain.addValidator(this::validateSourceDestinationDirectoryIds);

        // Step 2.
        validationChain.addValidator(this::validateUserFiles);

        // Step 3.
        validationChain.addValidator(this::validateSourceDirectoryBelongsToUser);

        // Step 4.
        validationChain.addValidator(this::validateDestinationDirectoryBelongsToUser);

        // Step 5.
        validationChain.addValidator(this::validateFilesInSameDirectory);

        String errorMessage = validationChain.validate();
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-7, errorMessage);

        // Step 6.
        int updateCount = fileMetadataMapper.batchSetDirectoryId(destinationDirectoryId, fileIds);

        if (updateCount == fileIds.size())
            return ServiceResponse.buildSuccessResponse(true);
        else
            return ServiceResponse.buildErrorResponse(-11, "Internal error, see logs for more information.");
    }

    @Override
    @Transactional
    public ServiceResponse<Boolean> deleteFiles(@Validated DeleteFilesParam deleteFilesParam)
    {
        // Steps:
        // 1. Validate userId-fileId pairs, report resource authorization exception if there is any userId-fileId not match.
        // 2. Validate if the source directory (with ID = sourceDirectoryId) belongs to the user, report resource authorization exception if not.
        // 3. Validate if all these files are in the same directory, whose ID matches moveFilesParam.sourceDirectoryId.
        // 4. Delete them all from both database and MinIO.

        ValidationChain<DeleteFilesParam> validationChain = new ValidationChain<>(deleteFilesParam);
        validationChain.addValidator(this::validateUserFiles);
        validationChain.addValidator(this::validateSourceDirectoryBelongsToUser);
        validationChain.addValidator(this::validateFilesInSameDirectory);

        String errorMessage = validationChain.validate();
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-7, errorMessage);

        // TODO: Delete files.

        return null;
    }
}
