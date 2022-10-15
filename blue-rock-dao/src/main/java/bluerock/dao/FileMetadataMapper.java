package bluerock.dao;

import bluerock.domain.FileMetadata;
import bluerock.params.CanUploadFileParam;
import bluerock.params.ShowDirectoryAndFileParam;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataMapper
{
    List<FileMetadata> showFilesByConditions(ShowDirectoryAndFileParam showFileParam);
    int countFileByCondition(CanUploadFileParam canUploadFileParam);
    FileMetadata getFileMetadataById(long id);
    int insertNew(FileMetadata fileMetadata);
}