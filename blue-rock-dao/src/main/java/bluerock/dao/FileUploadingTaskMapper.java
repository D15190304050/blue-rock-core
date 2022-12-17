package bluerock.dao;

import bluerock.domain.FileUploadingTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadingTaskMapper
{
    int insert(FileUploadingTask fileUploadingTask);
    int setTaskStateById(long id, int state);
    FileUploadingTask getTaskById(long id);
}