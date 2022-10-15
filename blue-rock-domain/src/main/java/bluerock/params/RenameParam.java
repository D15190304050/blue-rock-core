package bluerock.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RenameParam
{
    @Min(value = 1, message = "User ID must be a positive integer.")
    private long userId;

    @Min(value = 1, message = "File/directory ID must be a positive integer.")
    private long id;

    @NotBlank(message = ValidationMessages.NAME_NOT_EMPTY)
    private String newName;
}
