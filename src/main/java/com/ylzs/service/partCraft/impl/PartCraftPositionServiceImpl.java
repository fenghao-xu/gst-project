package com.ylzs.service.partCraft.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.core.model.SuperEntity;
import com.ylzs.dao.partCraft.PartCraftPositionDao;
import com.ylzs.entity.partCraft.PartCraftPosition;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCraft.IPartCraftPositionService;
import com.ylzs.vo.partCraft.PartCraftPositionVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service()
public class PartCraftPositionServiceImpl extends OriginServiceImpl<PartCraftPositionDao, PartCraftPosition> implements IPartCraftPositionService {

    @Resource
    private PartCraftPositionDao partCraftPositionDao;

    @Override
    public Integer getNumberByPartCraftMainRandomCode(Long partCraftMainRandomCode) {
        return partCraftPositionDao.getNumberByPartCraftMainRandomCode(partCraftMainRandomCode);
    }

    @Override
    public List<PartCraftPosition> getPartCraftPositionMainList(Long partCraftMainRandomCode) {
        List<PartCraftPosition> list = new ArrayList<>();
        try {
            list = partCraftPositionDao.getPartCraftPositionMainList(partCraftMainRandomCode);
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftPosition> getPartCraftPositionMainList(Long partCraftMainCode, Integer status) {
        List<PartCraftPosition> list = new ArrayList<>();
        try {
            QueryWrapper<PartCraftPosition> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(PartCraftPosition::getPartCraftMainRandomCode, partCraftMainCode)
                    .eq(SuperEntity::getStatus, status);
            list = partCraftPositionDao.selectList(queryWrapper);
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftPosition> getPartCraftPositionRandomList(List<Long> randomCodes) {
        List<PartCraftPosition> list = new ArrayList<>();
        try {

            list = partCraftPositionDao.getPartCraftPositionRandomList(randomCodes);
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftPositionVo> getPartCraftPositionVoList(Long partCraftMainRandomCode, Integer status) {
        List<PartCraftPositionVo> list = new ArrayList<>();
        try {
            list = partCraftPositionDao.getPartCraftPositionVoList(partCraftMainRandomCode, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<Long, List<PartCraftPositionVo>> getPartCraftPositionGroupMainRandomCodeByList(List<Long> randomCodes) {
        Map<Long, List<PartCraftPositionVo>> groupByMap = new HashMap<>();
        try {
            if (null != randomCodes && randomCodes.size() > 0) {
                List<PartCraftPositionVo> list = partCraftPositionDao.getRandomCodePartCraftPositionBatchList(randomCodes);
                if (ObjectUtils.isNotEmptyList(list)) {
                    groupByMap = list.stream().collect(Collectors.groupingBy(PartCraftPositionVo::getPartCraftMainRandomCode));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupByMap;
    }
}