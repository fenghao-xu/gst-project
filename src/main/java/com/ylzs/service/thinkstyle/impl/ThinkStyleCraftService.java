package com.ylzs.service.thinkstyle.impl;


import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.dao.thinkstyle.ThinkStyleCraftDao;
import com.ylzs.entity.thinkstyle.ThinkStyleCraft;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.thinkstyle.IThinkStyleCraftService;
import com.ylzs.vo.thinkstyle.CustomFlowNumVo;
import com.ylzs.vo.thinkstyle.ThinkStyleCraftVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：lyq
 * @description：智库款工艺工序
 * @date ：2020-03-05 18:40
 */
@Service
public class ThinkStyleCraftService extends OriginServiceImpl<ThinkStyleCraftDao, ThinkStyleCraft> implements
        IThinkStyleCraftService {
    @Resource
    private ThinkStyleCraftDao thinkStyleCraftDao;

    @Override
    public ThinkStyleCraftVo getThinkStyleCraftVo(ThinkStyleCraft obj) {
        ThinkStyleCraftVo result = new ThinkStyleCraftVo();
        try {
            BeanUtils.copyProperties(obj,result);
        } catch (Exception e) {

        }
        return result;
    }

    @Override
    public List<CustomFlowNumVo> getCustomFlowNum(String clothesCategoryCode, String[] craftCodes) {

        return thinkStyleCraftDao.getCustomFlowNum(clothesCategoryCode, craftCodes, BusinessConstants.BusinessType.CUSTOMIZE);
    }


}