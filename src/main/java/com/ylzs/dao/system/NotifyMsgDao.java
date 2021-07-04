package com.ylzs.dao.system;

import com.ylzs.entity.system.NotifyMsg;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 通知消息
 */
public interface NotifyMsgDao {
    Integer addNotifyMsg(NotifyMsg notifyMsg);

    Integer addNotifyMsgs(@Param("notifyMsgs") List<NotifyMsg> notifyMsgs);

    Integer deleteNotifyMsg(Long id);
    Integer updateNotifyMsg(NotifyMsg notifyMsg);
    List<NotifyMsg> getNotifyMsgById(Long id);
    List<NotifyMsg> getNotifyMsgByPage(@Param("isRead") Boolean isRead,
                                       @Param("keywords") String keywords, @Param("beginDate") Date beginDate,
                                       @Param("endDate") Date endDate,
                                       @Param("readUser") String readUser);
}
