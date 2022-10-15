package bluerock.api;

import bluerock.domain.FileMetadata;
import bluerock.params.BatchMultipartFileParam;
import bluerock.params.MultipartFileParam;
import dataworks.web.commons.ServiceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface IFileOperationService
{
    ServiceResponse<String> uploadFile(MultipartFileParam multipartFileParam);
    ServiceResponse<Boolean> uploadFiles(BatchMultipartFileParam batchMultipartFileParam);
    boolean deleteFiles(List<FileMetadata> fileMetadataList);
}
