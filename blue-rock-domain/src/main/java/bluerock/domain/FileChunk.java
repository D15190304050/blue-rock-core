package bluerock.domain;

import bluerock.states.FileChunkState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileChunk
{
    private long id;
    private long taskId;
    private long sliceIndex;
    private String objectName;
    private long byteCount;
    private Date uploadingTime;
    private int state;

    public static boolean allFinished(List<FileChunk> chunks)
    {
        for (FileChunk chunk : chunks)
        {
            if (chunk.state == FileChunkState.UPLOADING)
                return false;
        }

        return true;
    }
}