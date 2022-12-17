package bluerock.states;

public enum FileMetadataState
{
    UPLOADING_UNFINISHED(-1),
    ACCESSIBLE(0),
    IN_TRASH(1),
    WAITING_FOR_DELETION(2),
    ;

    private final int code;

    FileMetadataState(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static FileMetadataState getStateByCode(int code)
    {
        for (FileMetadataState state : values())
        {
            if (state.code == code)
                return state;
        }
        return null;
    }
}
