package bluerock.controllers;

import bluerock.api.IFileOperationService;
import bluerock.params.BatchMultipartFileParam;
import bluerock.params.MultipartFileParam;
import dataworks.web.commons.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/fileOps")
public class FileOperationController
{
    @Autowired
    private IFileOperationService fileOperationService;

    @PostMapping("/uploadFile")
    public ServiceResponse<String> uploadFile(MultipartFileParam multipartFileParam)
    {
        return fileOperationService.uploadFile(multipartFileParam);
    }

    public ServiceResponse<Boolean> uploadFiles(BatchMultipartFileParam batchMultipartFileParam)
    {
        return fileOperationService.uploadFiles(batchMultipartFileParam);
    }
}
