package bluerock.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetChunkStatesParam
{
    @Min(value = 1, message = ValidationMessages.USER_ID_POSITIVE)
    long userId;

    @Min(value = 1, message = ValidationMessages.TASK_ID_POSITIVE)
    long taskId;
}
