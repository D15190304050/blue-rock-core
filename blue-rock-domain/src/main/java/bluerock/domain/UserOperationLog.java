package bluerock.domain;

public class UserOperationLog
{
    private long id;

    private int operationTypeId;

    private long userId;

    private String detail;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public int getOperationTypeId()
    {
        return operationTypeId;
    }

    public void setOperationTypeId(int operationTypeId)
    {
        this.operationTypeId = operationTypeId;
    }

    public long getUserId()
    {
        return userId;
    }

    public void setUserId(long userId)
    {
        this.userId = userId;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail == null ? null : detail.trim();
    }
}