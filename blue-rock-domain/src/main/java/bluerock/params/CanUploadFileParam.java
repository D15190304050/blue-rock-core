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
    @Min(value = 1, message = "User ID must be a positive integer.")
    private long userId;

    @NotBlank(message = ValidationMessages.NAME_NOT_EMPTY)
    private String name;

    private Long parentDirectoryId;
}
