package bluerock.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileUploadResult
{
    /**
     * Null if unsuccessful, otherwise, ID of the file in the database.
     */
    private Long fileId;

    /**
     * Name of the bucket where the file is in.
     */
    private String bucketName;

    /**
     * Name of the file.
     */
    private String fileName;

    /**
     * If the upload operation is success.
     */
    private boolean success;
}
