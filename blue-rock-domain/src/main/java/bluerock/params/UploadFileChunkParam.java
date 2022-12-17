package bluerock.params;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UploadFileChunkParam
{
    @Min(value = 1, message = ValidationMessages.USER_ID_POSITIVE)
    private long userId;

    @Min(value = 1, message = ValidationMessages.TASK_ID_POSITIVE)
    private long taskId;

    @Min(value = 0, message = ValidationMessages.SLICE_INDEX_NON_NEGATIVE)
    private long sliceIndex;

    @NotNull
    @JsonIgnore
    private MultipartFile chunk;
}
