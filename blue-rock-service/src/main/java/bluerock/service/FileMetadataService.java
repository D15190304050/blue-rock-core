package bluerock.service;

import bluerock.api.IFileMetadataService;
import bluerock.dao.FileMetadataMapper;
import bluerock.dao.UserDirectoryMapper;
import bluerock.domain.FileMetadata;
import bluerock.domain.UserDirectory;
import bluerock.params.ShowDirectoryAndFileParam;
import dataworks.params.ArgumentValidator;
import dataworks.web.commons.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileMetadataService implements IFileMetadataService
{
    @Autowired
    private FileMetadataMapper fileMetadataMapper;

    @Autowired
    private UserUserDirectoryService userDirectoryService;

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
    public String getFilePathByFileId(long id)
    {
        FileMetadata fileMetadata = fileMetadataMapper.getFileMetadataById(id);
        Long directoryId = fileMetadata.getDirectoryId();

        String fileName = fileMetadata.getFileName();

        String directoryPath = userDirectoryService.getDirectoryPathById(directoryId);
        return directoryPath + fileName;
    }

    @Override
    public String generateUserFilePath(long userId, Long parentDirectoryId, String fileName)
    {
        String directoryPath = userDirectoryService.getDirectoryPathById(parentDirectoryId);
        return userId + "-" + directoryPath + fileName;
    }

    @Override
    public List<FileMetadata> getFileIdsInDirectoryIds(long userId, List<Long> directoryIds)
    {
        return fileMetadataMapper.getFilesInDirectories(userId, directoryIds);
    }
}
