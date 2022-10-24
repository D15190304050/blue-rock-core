package bluerock.controllers;

import bluerock.api.IFileIOService;
import bluerock.params.BatchMultipartFileParam;
import bluerock.params.MultipartFileParam;
import dataworks.web.commons.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/fileOps")
public class FileIOController
{
    @Autowired
    private IFileIOService fileIOService;

    @PostMapping("/uploadFile")
    public ServiceResponse<String> uploadFile(MultipartFileParam multipartFileParam)
    {
        return fileIOService.uploadFile(multipartFileParam);
    }

    public ServiceResponse<Boolean> uploadFiles(BatchMultipartFileParam batchMultipartFileParam)
    {
        return fileIOService.uploadFiles(batchMultipartFileParam);
    }
}
