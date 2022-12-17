package bluerock.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileUploadingState
{
    long taskId;
    List<FileChunkState> fileChunkStates;
}
