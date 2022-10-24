package bluerock.service;

import bluerock.api.IFileIOService;
import bluerock.api.IUserDirectoryService;
import bluerock.api.IFileMetadataService;
import bluerock.dao.UserDirectoryMapper;
import bluerock.domain.FileMetadata;
import bluerock.domain.UserDirectory;
import bluerock.model.StorageObject;
import bluerock.params.DeleteParam;
import bluerock.params.CreateDirectoryParam;
import bluerock.params.RenameParam;
import bluerock.params.ShowDirectoryAndFileParam;
import dataworks.params.ArgumentValidator;
import dataworks.web.commons.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDirectoryService implements IUserDirectoryService
{
    @Autowired
    private UserDirectoryMapper userDirectoryMapper;

    @Autowired
    private IFileMetadataService fileMetadataService;

    @Autowired
    private IFileIOService fileIOService;

    @Override
    public ServiceResponse<List<UserDirectory>> showDirectories(ShowDirectoryAndFileParam showParam)
    {
        List<UserDirectory> userDirectories = getUserDirectoriesByConditions(showParam);
        return ServiceResponse.buildSuccessResponse(userDirectories);
    }

    private List<UserDirectory> getUserDirectoriesByConditions(ShowDirectoryAndFileParam showParam)
    {
        ArgumentValidator.requireNonNull(showParam, "showParam");
        showParam.prepareForDirectory();

        return userDirectoryMapper.showDirectoriesByConditions(showParam);
    }

    @Override
    public ServiceResponse<List<StorageObject>> showContentsInDirectory(ShowDirectoryAndFileParam showParam)
    {
        List<UserDirectory> userDirectories = getUserDirectoriesByConditions(showParam);
        List<FileMetadata> fileMetadataList = fileMetadataService.getFileMetadataListByConditions(showParam);

        List<StorageObject> storageObjects = new ArrayList<>();

        for (UserDirectory userDirectory : userDirectories)
        {
            StorageObject storageObject = new StorageObject(userDirectory);
            storageObjects.add(storageObject);
        }

        for (FileMetadata fileMetadata : fileMetadataList)
        {
            StorageObject storageObject = new StorageObject(fileMetadata);
            storageObjects.add(storageObject);
        }

        return ServiceResponse.buildSuccessResponse(storageObjects);
    }

    @Override
    @Transactional
    public ServiceResponse<Boolean> createDirectory(@Validated CreateDirectoryParam createParam)
    {
        int directoryCount = userDirectoryMapper.countDirectoryByNameAndParent(createParam);
        if (directoryCount > 0)
            return new ServiceResponse<>(-2, false, false, "Directory exists", null);

        long parentDirectoryId = createParam.getParentDirectoryId();
        String directoryPathSuffix = createParam.getName() + "/";
        String path;
        UserDirectory parentDirectory = userDirectoryMapper.getDirectoryById(parentDirectoryId);
        path = parentDirectory.getPath() + directoryPathSuffix;

        UserDirectory directoryToInsert = new UserDirectory();
        directoryToInsert.setParentId(parentDirectoryId);
        directoryToInsert.setName(createParam.getName());
        directoryToInsert.setUserId(createParam.getUserId());
        directoryToInsert.setPath(path);
        userDirectoryMapper.insert(directoryToInsert);

        return ServiceResponse.buildSuccessResponse(true);
    }

    @Override
    @Transactional
    public ServiceResponse<Boolean> renameDirectory(@Validated RenameParam renameParam)
    {
        int directoryCount = userDirectoryMapper.countDirectoryByIdAndUserId(renameParam.getId(), renameParam.getUserId());
        if (directoryCount > 0)
        {
            userDirectoryMapper.updateNameById(renameParam);
            return ServiceResponse.buildSuccessResponse(true);
        }
        else
            return new ServiceResponse<>(-3, false, false, "Directory does not exists", null);
    }

    @Override
    public String getDirectoryPathById(long id)
    {
        return userDirectoryMapper.getDirectoryById(id).getPath();
    }

    @Override
    public ServiceResponse<Boolean> canDelete(DeleteParam deleteParam)
    {
        boolean can = userDirectoryMapper.countDirectoryByIdAndUserId(deleteParam.getId(), deleteParam.getUserId()) == 1;
        return ServiceResponse.buildSuccessResponse(can);
    }

    public List<Long> getAllChildrenIds(long id)
    {
        String childrenIdString = userDirectoryMapper.findChildrenDirectoryIds(id);
        String[] childrenIdStringArray = childrenIdString.split(",");
        List<Long> childrenIds = new ArrayList<>();

        for (String idString : childrenIdStringArray)
            childrenIds.add(Long.parseLong(idString));

        return childrenIds;
    }

    /**
     * Delete the directory and its children directories (if there is) and files in them (if there is).
     * @param deleteParam
     * @return
     */
    @Override
    @Transactional
    public ServiceResponse<Boolean> deleteDirectory(DeleteParam deleteParam)
    {
        // Find the ids of the children of the directory to delete.
        // Find ids of the files in those directories.
        // Delete the records of the directories in the database.
        // Delete the records of the files in the database.
        // Delete the files in the minio.

        long userId = deleteParam.getUserId();

        List<Long> childrenDirectoryIds = getAllChildrenIds(deleteParam.getId());

        List<FileMetadata> fileMetadataList = fileMetadataService.getFileIdsInDirectoryIds(userId, childrenDirectoryIds);
        boolean deletionResult = fileIOService.deleteFiles(fileMetadataList);

        int directoryDeletionCount = userDirectoryMapper.deleteDirectoriesByIdsAndUserId(userId, childrenDirectoryIds);

        boolean result = directoryDeletionCount == childrenDirectoryIds.size() && deletionResult;

        if (result)
            return ServiceResponse.buildSuccessResponse(true);
        else
            return ServiceResponse.buildErrorResponse(-5, "Error when executing deletion operations, please check the log...");
    }
}
