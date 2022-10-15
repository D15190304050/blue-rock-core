package bluerock.dao;

import bluerock.domain.FilePart;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilePartMapper
{
    int countFilePartsByTaskId(long taskId);
    int insert(FilePart filePart);
    List<String> getPartNamesByTaskId(long taskId);
}