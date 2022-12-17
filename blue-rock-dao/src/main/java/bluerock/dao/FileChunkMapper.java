package bluerock.dao;

import bluerock.domain.FileChunk;
import bluerock.results.FileChunkState;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileChunkMapper
{
    int countFilePartsByTaskId(long taskId);
    int insert(FileChunk fileChunk);
    int setChunkUploaded(long chunkId);
    List<String> getChunkNamesByTaskId(long taskId);
    int getChunkStateByTaskIdAndSliceIndex(@Param("taskId") long taskId, @Param("sliceIndex") long sliceIndex);
    List<FileChunk> getChunksByTaskId(long taskId);
    List<FileChunkState> getChunkStatesByTaskId(long taskId);
}