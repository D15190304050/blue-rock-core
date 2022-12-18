package bluerock.dao;

import bluerock.domain.UserDirectory;
import bluerock.params.CreateDirectoryParam;
import bluerock.params.RenameParam;
import bluerock.params.ShowDirectoryAndFileParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDirectoryMapper
{
    List<UserDirectory> showDirectoriesByConditions(ShowDirectoryAndFileParam showDirectoryParam);
    int countDirectoryByNameAndParent(CreateDirectoryParam createParam);
    int insert(UserDirectory userDirectory);
    UserDirectory getDirectoryById(long id);
    int countDirectoryByIdAndUserId(@Param("id") long id, @Param("userId") long userId);
    int updateNameById(RenameParam renameParam);
    String findChildrenDirectoryIds(long rootDirectoryId);
    int deleteDirectoriesByIdsAndUserId(@Param("userId") long userId, @Param("ids") List<Long> ids);
    Long getRootDirectoryIdByUserId(long userId);
}