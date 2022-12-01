package bluerock.dao;

import bluerock.domain.FileChunk;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileChunkMapper
{
    int countFilePartsByTaskId(long taskId);
    int insert(FileChunk fileChunk);
    List<String> getPartNamesByTaskId(long taskId);
}