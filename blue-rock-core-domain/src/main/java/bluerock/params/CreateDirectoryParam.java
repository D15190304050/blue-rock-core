package bluerock.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@NotNull
public class CreateDirectoryParam
{
    @Min(value = 1, message = ValidationMessages.USER_ID_POSITIVE)
    private long userId;

    /**
     * Note, some symbols can not be contained in the name of a directory: "\", "/", ...
     */
    @NotBlank(message = ValidationMessages.NAME_NOT_EMPTY)
    private String name;

    private long parentDirectoryId;
}
