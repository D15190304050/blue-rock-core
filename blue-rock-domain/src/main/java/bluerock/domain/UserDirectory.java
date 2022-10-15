package bluerock.domain;

import java.util.Date;

public class UserDirectory
{
    private long id;

    private Long parentId;

    private long userId;

    private String name;

    private String path;

    private Date creationTime;

    private Date updateTime;

    private int inTrash;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public long getUserId()
    {
        return userId;
    }

    public void setUserId(long userId)
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name == null ? null : name.trim();
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path == null ? null : path.trim();
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

    public int getInTrash()
    {
        return inTrash;
    }

    public void setInTrash(int inTrash)
    {
        this.inTrash = inTrash;
    }
}