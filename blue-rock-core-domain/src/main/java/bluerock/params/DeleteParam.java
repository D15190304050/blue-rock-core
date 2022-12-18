package bluerock.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeleteParam
{
    @Min(value = 1, message = ValidationMessages.USER_ID_POSITIVE)
    private long userId;
    private long id;
}
