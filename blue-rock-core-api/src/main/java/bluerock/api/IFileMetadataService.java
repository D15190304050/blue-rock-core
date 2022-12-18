package bluerock.api;

import bluerock.domain.FileMetadata;
import bluerock.params.DeleteFilesParam;
import bluerock.params.MoveFilesParam;
import bluerock.params.RenameParam;
import bluerock.params.ShowDirectoryAndFileParam;
import dataworks.web.commons.ServiceResponse;

import java.util.List;

public interface IFileMetadataService
{
    ServiceResponse<List<FileMetadata>> showFiles(ShowDirectoryAndFileParam showFileParam);
    List<FileMetadata> getFileMetadataListByConditions(ShowDirectoryAndFileParam showFileParam);
    String getFilePathByFileId(long userId, long id);
    String generateUserFilePath(long userId, long parentDirectoryId, String fileName);
    List<FileMetadata> getFileIdsInDirectoryIds(long userId, List<Long> directoryIds);
    ServiceResponse<FileMetadata> getFileMetadataById(long userId, long id);
    ServiceResponse<Boolean> renameFile(RenameParam renameParam);
    ServiceResponse<Boolean> moveFiles(MoveFilesParam moveFilesParam);
    ServiceResponse<Boolean> deleteFiles(DeleteFilesParam deleteFilesParam);
}
