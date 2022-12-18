package bluerock.api;

import bluerock.domain.FileMetadata;
import bluerock.params.*;
import bluerock.results.FileUploadingState;
import dataworks.web.commons.ServiceResponse;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface IFileIOService
{
    ServiceResponse<String> uploadFile(MultipartFileParam multipartFileParam);
    ServiceResponse<Long> initFileUploadingTask(InitFileUploadingTaskParam initFileUploadingTaskParam);
    ServiceResponse<Boolean> uploadFileChunk(UploadFileChunkParam uploadFileChunkParam);
    ServiceResponse<Boolean> mergeChunks(MergeChunksParam mergeChunksParam);
    ServiceResponse<FileUploadingState> getChunkStates(GetChunkStatesParam getChunkStatesParam);
    ServiceResponse<Long> getSliceByteCount();
    boolean deleteFiles(List<FileMetadata> fileMetadataList);
}
