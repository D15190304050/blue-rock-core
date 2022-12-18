package bluerock.dao;

import bluerock.domain.UserLoginLog;

import java.util.List;

public interface UserLoginLogMapper
{
    int deleteByPrimaryKey(long id);

    int insert(UserLoginLog row);

    UserLoginLog selectByPrimaryKey(long id);

    List<UserLoginLog> selectAll();

    int updateByPrimaryKey(UserLoginLog row);
}