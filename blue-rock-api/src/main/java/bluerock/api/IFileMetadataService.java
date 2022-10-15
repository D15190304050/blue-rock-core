package bluerock.api;

import bluerock.domain.FileMetadata;
import bluerock.params.ShowDirectoryAndFileParam;
import dataworks.web.commons.ServiceResponse;

import java.util.List;

public interface IFileMetadataService
{
    ServiceResponse<List<FileMetadata>> showFiles(ShowDirectoryAndFileParam showFileParam);
    List<FileMetadata> getFileMetadataListByConditions(ShowDirectoryAndFileParam showFileParam);
    String getFilePathByFileId(long id);
    String generateUserFilePath(long userId, Long parentDirectoryId, String fileName);
    List<FileMetadata> getFileIdsInDirectoryIds(long userId, List<Long> directoryIds);
}
