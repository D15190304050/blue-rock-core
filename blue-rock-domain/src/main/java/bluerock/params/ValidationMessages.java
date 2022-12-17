package bluerock.params;

public class ValidationMessages
{
    private ValidationMessages()
    {}

    public static final String NAME_NOT_EMPTY = "Name can not be null or empty.";
    public static final String USER_ID_POSITIVE = "User ID must be a positive integer.";
    public static final String FILE_DIRECTORY_ID_POSITIVE = "File/directory ID must be a positive integer.";
    public static final String FILE_DIRECTORY_IDS_NOT_EMPTY = "At least 1 file/directory ID should be provided.";
    public static final String CHUNK_COUNT_POSITIVE = "Chunk count must be positive.";
    public static final String TASK_ID_POSITIVE = "File uploading task ID must be positive.";
    public static final String SLICE_INDEX_NON_NEGATIVE = "File slice index must be non-negative.";
}
