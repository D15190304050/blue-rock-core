package bluerock.dao;

import bluerock.domain.FileMetadata;
import bluerock.params.CanUploadFileParam;
import bluerock.params.ShowDirectoryAndFileParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataMapper
{
    List<FileMetadata> showFilesByConditions(ShowDirectoryAndFileParam showFileParam);
    int countFileByCondition(CanUploadFileParam canUploadFileParam);
    FileMetadata getFileMetadataById(long id);
    int insertNew(FileMetadata fileMetadata);
    List<FileMetadata> getFilesInDirectories(@Param("userId") long userId, @Param("directoryIds") List<Long> directoryIds);
    int deleteFilesByIds(@Param("userId") long userId, @Param("fileIds") List<Long> fileIds);
}