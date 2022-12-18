package bluerock.controllers;

import bluerock.api.IFileIOService;
import bluerock.params.*;
import bluerock.results.FileUploadingState;
import dataworks.web.commons.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/fileOps")
public class FileIOController
{
    @Autowired
    private IFileIOService fileIOService;

    /**
     * Used only for file with size <= 10 MB.
     * @param multipartFileParam
     * @return
     */
    @PostMapping("/uploadFile")
    public ServiceResponse<String> uploadFile(MultipartFileParam multipartFileParam)
    {
        return fileIOService.uploadFile(multipartFileParam);
    }

    @PostMapping("/init-task")
    public ServiceResponse<Long> initFileUploadingTask(@RequestBody InitFileUploadingTaskParam initFileUploadingTaskParam)
    {
        return fileIOService.initFileUploadingTask(initFileUploadingTaskParam);
    }

    @PostMapping("/upload-chunk")
    public ServiceResponse<Boolean> uploadFileChunk(@RequestBody UploadFileChunkParam uploadFileChunkParam)
    {
        return fileIOService.uploadFileChunk(uploadFileChunkParam);
    }

    @PostMapping("/merge")
    public ServiceResponse<Boolean> mergeChunks(@RequestBody MergeChunksParam mergeChunksParam)
    {
        return fileIOService.mergeChunks(mergeChunksParam);
    }

    @PostMapping("/chunk-states")
    public ServiceResponse<FileUploadingState> getChunkStates(@RequestBody GetChunkStatesParam getChunkStatesParam)
    {
        return fileIOService.getChunkStates(getChunkStatesParam);
    }

    @GetMapping("/slice-size")
    public ServiceResponse<Long> getSliceByteCount()
    {
        return fileIOService.getSliceByteCount();
    }

    // TODO: BatchUploadFiles()
}
