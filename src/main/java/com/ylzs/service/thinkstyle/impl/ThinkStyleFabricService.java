package com.ylzs.service.thinkstyle.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.dao.thinkstyle.ThinkStyleFabricDao;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.thinkstyle.ThinkStyleFabric;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.thinkstyle.IThinkStyleFabricService;
import com.ylzs.vo.thinkstyle.ThinkStyleFabricVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ：lyq
 * @description：TODO
 * @date ：2020-03-05 18:44
 */
@Service
public class ThinkStyleFabricService extends OriginServiceImpl<ThinkStyleFabricDao, ThinkStyleFabric> implements IThinkStyleFabricService {
    @Resource
    private ThinkStyleFabricDao thinkStyleFabricDao;

    @Resource
    private DictionaryDao dictionaryDao;

    @Override
    public List<ThinkStyleFabricVo> getThinkStyleFabricVos(Long styleRandomCode) {
        QueryWrapper<ThinkStyleFabric> fabricWrapper = new QueryWrapper<>();
        fabricWrapper.lambda().eq(ThinkStyleFabric::getStyleRandomCode, styleRandomCode).orderByAsc(ThinkStyleFabric::getLineNo);
        List<ThinkStyleFabric> thinkStyleFabrics = thinkStyleFabricDao.selectList(fabricWrapper);
        if(thinkStyleFabrics == null) {
            return null;
        }
        List<ThinkStyleFabricVo> thinkStyleFabricVos = thinkStyleFabrics.stream().map(this::getThinkStyleFabricVo).collect(Collectors.toList());
        Map<String, Dictionary> dictionaryMap = dictionaryDao.getDictionaryMap("patternSymmetry");
        if(dictionaryMap != null && ObjectUtils.isNotEmptyList(thinkStyleFabricVos)) {
            thinkStyleFabricVos.stream().forEach(x -> {
                if(StringUtils.isNotEmpty(x.getBarType()) && dictionaryMap.containsKey(x.getBarType())) {
                    x.setBarTypeName(dictionaryMap.get(x.getBarType()).getValueDesc());
                }
            });
        }
        return thinkStyleFabricVos;
    }

    private ThinkStyleFabricVo getThinkStyleFabricVo(ThinkStyleFabric obj) {
        ThinkStyleFabricVo result = new ThinkStyleFabricVo();
        try {
            BeanUtils.copyProperties(obj, result);
        } catch (Exception e) {

        }
        return result;


    }
}
