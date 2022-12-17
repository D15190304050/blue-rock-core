package bluerock.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InitFileUploadingTaskParam
{
    @Min(value = 1, message = ValidationMessages.USER_ID_POSITIVE)
    private long userId;

    @Min(value = 1, message = ValidationMessages.FILE_DIRECTORY_ID_POSITIVE)
    private long directoryId;

    @NotBlank
    private String fileName;

    @Min(value = 1, message = ValidationMessages.CHUNK_COUNT_POSITIVE)
    private long chunkCount;
}
