package com.ylzs.service.partCraft.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.core.model.SuperEntity;
import com.ylzs.dao.partCraft.PartCraftDesignPartsDao;
import com.ylzs.entity.partCraft.PartCraftDesignParts;
import com.ylzs.entity.thinkstyle.ThinkStyleCraft;
import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.entity.thinkstyle.ThinkStyleProcessRule;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCraft.IPartCraftDesignPartsService;
import com.ylzs.vo.partCraft.DesignPartVO;
import com.ylzs.vo.partCraft.PartCraftDesignPartsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service()
public class PartCraftDesignPartsServiceImpl extends OriginServiceImpl<PartCraftDesignPartsDao, PartCraftDesignParts> implements IPartCraftDesignPartsService {


    @Resource
    private PartCraftDesignPartsDao designPartsDao;

    @Override
    public List<PartCraftDesignParts> getDesignPartsMainList(Long partCraftMainCode) {
        List<PartCraftDesignParts> list = new ArrayList<>();
        try {
            list = designPartsDao.getDesignPartsMainList(partCraftMainCode);
        } catch (Exception ex) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public Integer getCountByDesignPartAndPosition(String designCode, String positionCode, String clothingCategory) {
        return designPartsDao.getCountByDesignPartAndPosition(designCode, positionCode, clothingCategory);
    }

    @Override
    public List<ThinkStyleCraft> getPartCraftByDesignPartAndPosition(String designCode, String positionCode, String clothingCategory) {
        return designPartsDao.getPartCraftByDesignPartAndPosition(designCode, positionCode, clothingCategory);
    }

    @Override
    public List<ThinkStyleProcessRule> getPartCraftRulesByDesignPartAndPosition(String designCode, String positionCode, String clothingCategory) {
        return designPartsDao.getPartCraftRulesByDesignPartAndPosition(designCode, positionCode, clothingCategory);
    }

    @Override
    public List<ThinkStylePart> getPartPriceAndTImeByDesignPartAndPosition(String designCode, String positionCode, String clothingCategory) {
        return designPartsDao.getPartPriceAndTImeByDesignPartAndPosition(designCode, positionCode, clothingCategory);
    }

    @Override
    public List<DesignPartVO> getDataByDesignCode(String designCode) {
        return designPartsDao.getDataByDesignCode(designCode);
    }

    @Override
    public List<PartCraftDesignParts> getDesignPartsMainList(Long partCraftMainCode, Integer status) {
        List<PartCraftDesignParts> list = new ArrayList<>();
        try {
            QueryWrapper<PartCraftDesignParts> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(PartCraftDesignParts::getPartCraftMainRandomCode, partCraftMainCode)
                    .eq(SuperEntity::getStatus, status);
            list = designPartsDao.selectList(queryWrapper);
        } catch (Exception ex) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftDesignParts> getDesignPartsRandomList(List<Long> randomCodes) {
        List<PartCraftDesignParts> list = new ArrayList<>();
        try {
            list = designPartsDao.getDesignPartsRandomList(randomCodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<PartCraftDesignPartsVo> getDesignPartsVoList(Long partCraftMainCode, Integer status) {
        List<PartCraftDesignPartsVo> list = new ArrayList<>();
        try {
            list = designPartsDao.getDesignPartsVoList(partCraftMainCode, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<Long, List<PartCraftDesignPartsVo>> getDesignPartGroupMainRandomCodeByList(List<Long> randomCodes) {
        Map<Long, List<PartCraftDesignPartsVo>> groupByMap = new HashMap<>();
        try {
            if (null != randomCodes && randomCodes.size() > 0) {
                List<PartCraftDesignPartsVo> list = designPartsDao.getDesignPartsVoBatchList(randomCodes);
                if (ObjectUtils.isNotEmptyList(list)) {
                    groupByMap = list.stream().collect(Collectors.groupingBy(PartCraftDesignPartsVo::getPartCraftMainRandomCode));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupByMap;
    }
}