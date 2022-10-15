package bluerock.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.function.Predicate;

@Slf4j
public class EasyMinioV1
{
    private final MinioClient minioClient;

    private final Predicate<Result<Item>> isFilePredicate;
    private final Predicate<Result<Item>> isDirectoryPredicate;

    public EasyMinioV1(String endpoint, String accessKey, String secretKey)
    {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        isFilePredicate = this::isFile;
        isDirectoryPredicate = this::isDirectory;
    }

    @SneakyThrows
    private boolean isFile(Result<Item> itemResult)
    {
        return !isDirectory(itemResult);
    }

    @SneakyThrows
    public boolean isDirectory(Result<Item> itemResult)
    {
        return itemResult.get().isDir();
    }

    public String upload(String bucketName, File file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return upload(bucketName, "", file);
    }

    public String upload(String bucketName, String directoryPath, File file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        if (directoryPath == null)
            directoryPath = "";
        else if (!directoryPath.endsWith("/"))
            directoryPath += "/";

        boolean bucketFound = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketFound)
        {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket \"" + bucketName + "\" created.");
        }

        String fileName = directoryPath + file.getName();
        String fileAbsolutePath = file.getAbsolutePath();

        minioClient.uploadObject(UploadObjectArgs.builder()
                .object(fileName)
                .filename(fileAbsolutePath)
                .build());

        log.info(fileAbsolutePath + " is successfully uploaded as object \"" + fileName + "\".");

        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileName)
                .build());
    }

    public void download(String bucketName, String objectName, String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        minioClient.downloadObject(DownloadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(filePath)
                .build());

        log.info(objectName + " is successfully downloaded.");
    }

    public Iterable<Result<Item>> listFileSystemEntries(String bucket, String path, boolean recursive)
    {
        if (path == null)
            path = "";
        else if (!path.endsWith("/"))
            path += "/";

        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(path)
                .recursive(recursive)
                .build());
    }

    public Iterable<Result<Item>> listTopLevelFileSystemEntries(String bucket, String path)
    {
        return listFileSystemEntries(bucket, path, false);
    }

    public Iterable<Result<Item>> listFileSystemEntriesRecursively(String bucket, String path)
    {
        return listFileSystemEntries(bucket, path, true);
    }

    private Iterable<Result<Item>> listFileSystemEntriesByType(String bucket, String path, boolean recursive, Predicate<Result<Item>> fileSystemTypePredicate) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        LinkedList<Result<Item>> files = new LinkedList<>();
        Iterable<Result<Item>> fileSystemEntries = listFileSystemEntries(bucket, path, recursive);
        for (Result<Item> fileSystemEntry : fileSystemEntries)
        {
            if (fileSystemTypePredicate.test(fileSystemEntry))
                files.addLast(fileSystemEntry);
        }

        return files;
    }

    public Iterable<Result<Item>> listFiles(String bucket, String path, boolean recursive) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return listFileSystemEntriesByType(bucket, path, recursive, isFilePredicate);
    }

    public Iterable<Result<Item>> listTopLevelFiles(String bucket, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return listFiles(bucket, path, false);
    }

    public Iterable<Result<Item>> listFilesRecursively(String bucket, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return listFiles(bucket, path, true);
    }

    public Iterable<Result<Item>> listDirectories(String bucket, String path, boolean recursive) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return listFileSystemEntriesByType(bucket, path, recursive, isDirectoryPredicate);
    }

    public Iterable<Result<Item>> listTopLevelDirectories(String bucket, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return listDirectories(bucket, path, false);
    }

    public Iterable<Result<Item>> listDirectoriesRecursively(String bucket, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return listDirectories(bucket, path, true);
    }
}
