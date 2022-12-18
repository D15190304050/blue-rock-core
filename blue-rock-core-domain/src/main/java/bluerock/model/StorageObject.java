package bluerock.model;

import bluerock.domain.FileMetadata;
import bluerock.domain.UserDirectory;
import dataworks.params.ArgumentValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StorageObject
{
    public static final int DIRECTORY = 0;
    public static final int FILE = 1;

    private int storageTypeId;
    private long userId;
    private long parentDirectoryId;
    private String name;
    private Date updateTime;
    private int inTrash;

    public StorageObject(UserDirectory userDirectory)
    {
        ArgumentValidator.requireNonNull(userDirectory, "userDirectory");

        this.storageTypeId = DIRECTORY;
        this.userId = userDirectory.getUserId();
        this.parentDirectoryId = userDirectory.getParentId();
        this.name = userDirectory.getName();
        this.updateTime = userDirectory.getUpdateTime();
        this.inTrash = userDirectory.getInTrash();
    }

    public StorageObject(FileMetadata fileMetadata)
    {
        ArgumentValidator.requireNonNull(fileMetadata, "fileMetadata");

        this.storageTypeId = FILE;
        this.userId = fileMetadata.getUserId();
        this.parentDirectoryId = fileMetadata.getDirectoryId();
        this.name = fileMetadata.getFileName();
        this.updateTime = fileMetadata.getUpdateTime();
        this.inTrash = fileMetadata.getInTrash();
    }

    public static String randomObjectName()
    {
        return UUID.randomUUID().toString();
    }
}
