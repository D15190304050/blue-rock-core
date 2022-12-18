package bluerock.params;

import java.util.List;

public interface IUserFileSourceDirectoryId
{
    long getUserId();
    List<Long> getFileIds();
    long getSourceDirectoryId();
}
