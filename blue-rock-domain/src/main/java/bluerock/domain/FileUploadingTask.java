package bluerock.domain;

import java.util.Date;

public class FileUploadingTask
{
    private long id;

    private long metadataId;

    private long totalByteCount;

    private long uploadedByteCount;

    private long userId;

    private Date creationTime;

    private Date updateTime;

    private int state;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getMetadataId()
    {
        return metadataId;
    }

    public void setMetadataId(long metadataId)
    {
        this.metadataId = metadataId;
    }

    public long getTotalByteCount()
    {
        return totalByteCount;
    }

    public void setTotalByteCount(long totalByteCount)
    {
        this.totalByteCount = totalByteCount;
    }

    public long getUploadedByteCount()
    {
        return uploadedByteCount;
    }

    public void setUploadedByteCount(long uploadedByteCount)
    {
        this.uploadedByteCount = uploadedByteCount;
    }

    public long getUserId()
    {
        return userId;
    }

    public void setUserId(long userId)
    {
        this.userId = userId;
    }

    public Date getCreationTime()
    {
        return creationTime;
    }

    public void setCreationTime(Date creationTime)
    {
        this.creationTime = creationTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }
}