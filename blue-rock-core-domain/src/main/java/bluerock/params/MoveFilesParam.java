package bluerock.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MoveFilesParam implements IUserFileSourceDirectoryId
{
    @Min(value = 1, message = ValidationMessages.USER_ID_POSITIVE)
    private long userId;

    @Size(min = 1, message = ValidationMessages.FILE_DIRECTORY_IDS_NOT_EMPTY)
    private List<Long> fileIds;

    @Min(value = 1, message = ValidationMessages.FILE_DIRECTORY_ID_POSITIVE)
    private long sourceDirectoryId;

    @Min(value = 1, message = ValidationMessages.FILE_DIRECTORY_ID_POSITIVE)
    private long destinationDirectoryId;
}
