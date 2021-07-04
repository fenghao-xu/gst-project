package com.ylzs.service.system;

import com.ylzs.entity.system.NotifyMsg;
import lombok.Data;

import java.util.Date;
import java.util.List;

public interface INotifyMsgService {
    Integer addNotifyMsg(NotifyMsg notifyMsg);
    Integer deleteNotifyMsg(Long id);
    Integer updateNotifyMsg(Long id, String currentUser);
    List<NotifyMsg> getNotifyMsgById(Long id);
    List<NotifyMsg> getNotifyMsgByPage(Boolean isRead, String keywords, Date beginDate, Date endDate, String readUser);
    NotifyMsgType[] getNotifyMsgType();

    /**
     * 通知消息类型
     */
    @Data
    static class NotifyMsgType {
        private Integer msgId;
        private String msgCaption;
    }
}
