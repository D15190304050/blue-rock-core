package bluerock.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EasyMinio
{
    private final MinioClient minioClient;

    public EasyMinio(EasyMinioProperties easyMinioProperties)
    {
        minioClient = MinioClient.builder()
                .endpoint(easyMinioProperties.getEndpoint())
                .credentials(easyMinioProperties.getAccessKey(), easyMinioProperties.getSecretKey())
                .build();
    }

    public MinioClient getMinioClient()
    {
        return minioClient;
    }

    public String getObjectUrl(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        String objectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .build());

        log.info("URL of object [" + bucketName + "/" + objectName + "] = " + objectUrl);
        return objectUrl;
    }

    public String uploadFileByStream(String bucketName, String fileName, InputStream inputStream) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        log.info("fileName = " + fileName);

        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(inputStream, inputStream.available(), -1)
                .build());

        inputStream.close();
        log.info("File upload successfully...");

        return getObjectUrl(bucketName, fileName);
    }

    public String uploadFileAndNotifyProgress(String bucketName, String fileName, InputStream inputStream, IProgressListener progressListener) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        log.info("fileName = " + fileName);

        ProgressInputStream progressInputStream = new ProgressInputStream(inputStream, progressListener);
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(progressInputStream, progressInputStream.available(), -1)
                .build());

        progressInputStream.close();
        log.info("File upload successfully...");

        return getObjectUrl(bucketName, fileName);
    }

    public String uploadFile(MultipartFile file, String bucketName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        return uploadFileByStream(bucketName, fileName, inputStream);
    }

    public String uploadFile(File file, String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        String fileName = file.getName();
        InputStream inputStream = new FileInputStream(file);
        return uploadFileByStream(bucketName, fileName, inputStream);
    }

    public void putObject(String bucketName, String objectName, InputStream inputStream) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, inputStream.available(), -1)
                .build());

        inputStream.close();
        log.info("File uploaded successfully...");
    }

    public void composeObjects(String bucketName, String objectName, List<String> sourceObjectNames) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        List<ComposeSource> sources = new ArrayList<>();
        for (String sourceObjectName : sourceObjectNames)
        {
            sources.add(ComposeSource.builder()
                    .bucket(bucketName)
                    .object(sourceObjectName)
                    .build());
        }

        minioClient.composeObject(ComposeObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .sources(sources)
                .build());

        log.info("Object composition successfully...");
    }
}
