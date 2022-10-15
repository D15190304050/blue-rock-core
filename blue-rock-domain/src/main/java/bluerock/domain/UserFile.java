package bluerock.domain;

public class UserFile
{
    private long id;

    private long userId;

    private long fileId;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getUserId()
    {
        return userId;
    }

    public void setUserId(long userId)
    {
        this.userId = userId;
    }

    public long getFileId()
    {
        return fileId;
    }

    public void setFileId(long fileId)
    {
        this.fileId = fileId;
    }
}