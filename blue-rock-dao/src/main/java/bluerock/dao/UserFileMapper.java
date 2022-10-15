package bluerock.dao;

import bluerock.domain.UserFile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFileMapper
{
    int insert(UserFile userFile);
}