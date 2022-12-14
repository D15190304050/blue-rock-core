package bluerock.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileMetadata
{
    private long id;
    private String bucketName;
    private String objectName;
    private String fileName;
    private String url;
    private long directoryId;
    private long userId;
    private Date updateTime;
    private int state;
}