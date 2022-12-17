package bluerock.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileUploadingTask
{
    private long id;
    private long metadataId;
    private long chunkCount;
    private long userId;
    private Date creationTime;
    private Date updateTime;
    private int state;
}