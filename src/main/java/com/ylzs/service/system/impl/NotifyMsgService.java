package com.ylzs.service.system.impl;

import com.ylzs.dao.system.NotifyMsgDao;
import com.ylzs.entity.system.NotifyMsg;
import com.ylzs.service.system.INotifyMsgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class NotifyMsgService implements INotifyMsgService {
    @Resource
    NotifyMsgDao notifyMsgDao;

    @Override
    public Integer addNotifyMsg(NotifyMsg notifyMsg) {
        return notifyMsgDao.addNotifyMsg(notifyMsg);
    }

    @Override
    public Integer deleteNotifyMsg(Long id) {
        return notifyMsgDao.deleteNotifyMsg(id);
    }

    @Override
    public Integer updateNotifyMsg(Long id, String currentUser) {
        NotifyMsg notifyMsg = new NotifyMsg();
        notifyMsg.setId(id);
        notifyMsg.setReadUser(currentUser);
        notifyMsg.setReadTime(new Date());
        notifyMsg.setIsRead(true);
        return notifyMsgDao.updateNotifyMsg(notifyMsg);
    }

    @Override
    public List<NotifyMsg> getNotifyMsgById(Long id) {
        return notifyMsgDao.getNotifyMsgById(id);
    }

    @Override
    public List<NotifyMsg> getNotifyMsgByPage(Boolean isRead, String keywords, Date beginDate, Date endDate, String readUser) {
        return notifyMsgDao.getNotifyMsgByPage(isRead, keywords, beginDate, endDate, readUser);
    }

    @Override
    public NotifyMsgType[] getNotifyMsgType() {
        NotifyMsgType msgType1 = new NotifyMsgType();
        msgType1.setMsgId(1);
        msgType1.setMsgCaption("工艺提交");

        NotifyMsgType msgType2 = new NotifyMsgType();
        msgType2.setMsgId(2);
        msgType2.setMsgCaption("工艺撤回");

        NotifyMsgType[] notifyMsgTypes = {msgType1,msgType2};
        return notifyMsgTypes;
    }
}
