package com.ylzs.service.thinkstyle.impl;

import com.ylzs.dao.thinkstyle.ThinkStyleCraftHistoryDao;
import com.ylzs.entity.thinkstyle.ThinkStyleCraftHistory;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.thinkstyle.IThinkStyleCraftHistoryService;
import com.ylzs.vo.thinkstyle.ThinkStyleCraftHistoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 智库款工序历史
 */
@Service
public class ThinkStyleCraftHistoryService extends OriginServiceImpl<ThinkStyleCraftHistoryDao, ThinkStyleCraftHistory> implements IThinkStyleCraftHistoryService {
    @Resource
    ThinkStyleCraftHistoryDao thinkStyleCraftHistoryDao;

    @Override
    public List<ThinkStyleCraftHistoryVo> getThinkStyleCraftHistoryVos(Long partRandomCode) {
        List<ThinkStyleCraftHistoryVo> craftHistoryVos = thinkStyleCraftHistoryDao.getThinkStyleCraftHistoryVos(partRandomCode);
        return craftHistoryVos;
    }

    private ThinkStyleCraftHistoryVo getThinkStyleCraftHistoryVo(ThinkStyleCraftHistory obj) {
        ThinkStyleCraftHistoryVo result = new ThinkStyleCraftHistoryVo();
        try {
            BeanUtils.copyProperties(obj, result);
        } catch (Exception e) {

        }
        return result;


    }

}
