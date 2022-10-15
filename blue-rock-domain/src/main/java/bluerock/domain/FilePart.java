package bluerock.domain;

import java.util.Date;

public class FilePart
{
    private long id;

    private long taskId;

    private String objectName;

    private long byteCount;

    private Date uploadingTime;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(long taskId)
    {
        this.taskId = taskId;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName(String objectName)
    {
        this.objectName = objectName == null ? null : objectName.trim();
    }

    public long getByteCount()
    {
        return byteCount;
    }

    public void setByteCount(long byteCount)
    {
        this.byteCount = byteCount;
    }

    public Date getUploadingTime()
    {
        return uploadingTime;
    }

    public void setUploadingTime(Date uploadingTime)
    {
        this.uploadingTime = uploadingTime;
    }
}