package bluerock.dao;

import bluerock.domain.UserOperationLog;

import java.util.List;

public interface UserOperationLogMapper
{
    int deleteByPrimaryKey(long id);

    int insert(UserOperationLog row);

    UserOperationLog selectByPrimaryKey(long id);

    List<UserOperationLog> selectAll();

    int updateByPrimaryKey(UserOperationLog row);
}