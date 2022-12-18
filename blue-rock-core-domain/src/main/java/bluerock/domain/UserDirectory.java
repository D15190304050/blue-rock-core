package bluerock.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDirectory
{
    private long id;

    private long parentId;

    private long userId;

    private String name;

    private String path;

    private Date creationTime;

    private Date updateTime;

    private int state;
}