package bluerock.minio;

public enum FileUploadingTaskState
{
    NOT_STARTED(0),
    UPLOADING(1),
    PAUSED(2),
    SUCCESS(3),
    FAILED(4),
    DELETED(5),
    ABORTED(6)
    ;

    private int code;

    FileUploadingTaskState(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static FileUploadingTaskState getStateByCode(int code)
    {
        for (FileUploadingTaskState state : values())
        {
            if (state.code == code)
                return state;
        }
        return null;
    }
}
