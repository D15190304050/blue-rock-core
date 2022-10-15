package bluerock.dao;

import bluerock.domain.FileUploadingTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadingTaskMapper
{
    int insert(FileUploadingTask row);
    int updateUploadedBytesById(@Param("id") long id, @Param("newUpdatedByteCount") long newUpdatedByteCount);
    int setTaskStateById(long id, int state);
    FileUploadingTask getTaskById(long id);
}