package bluerock.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CanUploadFileParam
{
    @Min(value = 1, message = ValidationMessages.USER_ID_POSITIVE)
    private long userId;

    @NotBlank(message = ValidationMessages.NAME_NOT_EMPTY)
    private String name;
    
    private long parentDirectoryId;
}
