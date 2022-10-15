package bluerock.api;

import bluerock.params.BatchMultipartFileParam;
import bluerock.params.MultipartFileParam;
import dataworks.web.commons.ServiceResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IFileOperationService
{
    ServiceResponse<String> uploadFile(MultipartFileParam multipartFileParam);
    ServiceResponse<Boolean> uploadFiles(BatchMultipartFileParam batchMultipartFileParam);
}
