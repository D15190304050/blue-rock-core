package bluerock.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@NotNull
public class MultipartFileParam
{
    @Min(value = 1, message = "User ID must be a positive integer.")
    private long userId;

    private Long parentDirectoryId;

    @NotNull
    private MultipartFile file;
}
