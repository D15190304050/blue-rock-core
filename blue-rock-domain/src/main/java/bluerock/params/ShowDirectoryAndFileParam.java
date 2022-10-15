package bluerock.params;

import bluerock.constants.SortOrders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShowDirectoryAndFileParam
{
    private static final HashMap<Integer, String> DIRECTORY_SORT_FIELD_MAP;
    private static final HashMap<Integer, String> FILE_SORT_FIELD_MAP;

    /**
     * ID of the user.
     */
    private long userId;

    /**
     * Pattern of the names of the directories (or files) to show. Show all directories (or files) if pass null.
     */
    private String pattern;

    /**
     * ID of the parent directory.
     */
    private Long parentDirectoryId;

    /**
     * Key of the sort field.
     */
    private Integer sortFieldKey;

    /**
     * Field to sort.
     */
    private String sortField;

    /**
     * Sort order, "ASC" / "DESC" only, ignore case.
     */
    private String sortOrder;

    static
    {
        DIRECTORY_SORT_FIELD_MAP = new HashMap<>()
        {{
            put(1, "name");
            put(2, "update_time");
        }};

        FILE_SORT_FIELD_MAP = new HashMap<>()
        {{
            put(1, "file_name");
            put(2, "update_time");
        }};
    }

    private void validateSortField()
    {
        if (sortFieldKey != null)
        {
            if (!DIRECTORY_SORT_FIELD_MAP.containsKey(sortFieldKey))
                throw new IllegalArgumentException("\"sortFieldKey\" can only be in " +
                        Arrays.toString(DIRECTORY_SORT_FIELD_MAP.keySet().toArray(new Integer[0])));
        }
    }

    private void validateSortOrder()
    {
        if (StringUtils.hasText(sortField))
        {
            if ((!SortOrders.matchAsc(sortOrder)) && (!SortOrders.matchDesc(sortOrder)))
                throw new IllegalArgumentException("Sort order can only be either \"ASC\" or \"DESC\"");
        }
    }

    private void validateSortArgs()
    {
        validateSortField();
        validateSortOrder();
    }

    public void prepareForDirectory()
    {
        validateSortArgs();

        sortField = DIRECTORY_SORT_FIELD_MAP.get(sortFieldKey);
    }

    public void prepareForFile()
    {
        validateSortArgs();

        sortField = FILE_SORT_FIELD_MAP.get(sortFieldKey);
    }
}
