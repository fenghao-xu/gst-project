package com.ylzs.dao.system;

import com.ylzs.entity.system.NotifyUser;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface NotifyUserDao {
    Integer addNotifyUser(NotifyUser notifyUser);

    Integer addNotifyUsers(@Param("notifyUsers") List<NotifyUser> notifyUsers);

    Integer deleteNotifyUser(Integer id);

    Integer updateNotifyUser(NotifyUser notifyUser);

    List<NotifyUser> getNotifyUserById(Integer id);

    List<NotifyUser> getNotifyUserByPage(@Param("keywords") String keywords,
                                         @Param("beginDate") Date beginDate,
                                         @Param("endDate") Date endDate,
                                         @Param("craftCategoryId") Integer craftCategoryId,
                                         @Param("msgType") Byte msgType,
                                         @Param("craftStdId") Long craftStdId);
}
