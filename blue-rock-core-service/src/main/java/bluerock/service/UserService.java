package bluerock.service;

import bluerock.api.IUserService;
import bluerock.dao.UserDirectoryMapper;
import bluerock.domain.UserDirectory;
import bluerock.minio.Bucket;
import bluerock.params.InitializeParam;
import dataworks.ExceptionInfoFormatter;
import dataworks.autoconfig.minio.EasyMinio;
import dataworks.autoconfig.web.LogArgumentsAndResponse;
import dataworks.web.commons.ServiceResponse;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@LogArgumentsAndResponse
public class UserService implements IUserService
{
    public static final String ROOT_DIRECTORY_NAME = "My Cloud Storage";
    public static final String ROOT_DIRECTORY_PATH = ROOT_DIRECTORY_NAME + "/";
    public static final long ROOT_DIRECTORY_PARENT_ID = 0L;

    @Autowired
    private EasyMinio easyMinio;

    @Autowired
    private UserDirectoryMapper userDirectoryMapper;

    private int createRootDirectoryForUser(long userId)
    {
        UserDirectory userDirectory = new UserDirectory();
        userDirectory.setUserId(userId);
        userDirectory.setPath(ROOT_DIRECTORY_PATH);
        userDirectory.setParentId(ROOT_DIRECTORY_PARENT_ID);
        userDirectory.setName(ROOT_DIRECTORY_NAME);

        return userDirectoryMapper.insert(userDirectory);
    }

    @Override
    @Transactional
    public ServiceResponse<Boolean> initialize(@Validated InitializeParam initializeParam)
    {
        // Create a bucket for the user.
        // Create a root directory for the user.
        long userId = initializeParam.getUserId();

        // Error when calling this method for an existing user.
        if (userDirectoryMapper.getRootDirectoryIdByUserId(userId) != null)
            return ServiceResponse.buildErrorResponse(-7, "Can not call initialize() for existing user.");

        // Try to insert the record of the root directory into the database.
        int insertionCount = createRootDirectoryForUser(userId);
        if (insertionCount == 0)
            return ServiceResponse.buildErrorResponse(-6, "Error when creating directory...");

        // TODO: Determine if we need to roll back (delete the record of the directory) in this case (catch the exception manually).

        // Try to create a bucket for the user in MinIO.
        String bucketName = Bucket.getBucketName(userId);
        try
        {
            easyMinio.createBucket(bucketName);
            return ServiceResponse.buildSuccessResponse(true);
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e)
        {
            String exceptionStackTrace = ExceptionInfoFormatter.formatMessageAndStackTrace(e);
            log.error("Error when trying to make bucket in MinIO..., " + exceptionStackTrace);
            return ServiceResponse.buildErrorResponse(-8, "Error when trying to make bucket in MinIO...");
        }
    }
}
