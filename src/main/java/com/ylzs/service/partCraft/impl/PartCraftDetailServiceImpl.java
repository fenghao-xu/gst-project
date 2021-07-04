package com.ylzs.service.partCraft.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.core.model.SuperEntity;
import com.ylzs.dao.partCraft.PartCraftDetailDao;
import com.ylzs.entity.partCraft.PartCraftDetail;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCraft.IPartCraftDetailService;
import com.ylzs.vo.partCraft.PartCraftDetailVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service()
public class PartCraftDetailServiceImpl extends OriginServiceImpl<PartCraftDetailDao, PartCraftDetail> implements IPartCraftDetailService {

    @Resource
    private PartCraftDetailDao detailDao;

    @Override
    public void updateCraftRemarkAndName(Map<String, Object> param) {
        detailDao.updateCraftRemarkAndName(param);
    }

    @Override
    public List<PartCraftDetail> getPartCraftDetailMainList(Long partCraftMainCode) {
        List<PartCraftDetail> list = new ArrayList<>();
        try {
            list = detailDao.getPartCraftDetailMainList(partCraftMainCode);
        } catch (Exception ex) {

        }
        return list;
    }

    @Override
    public List<PartCraftDetail> getPartCraftDetailMainList(Long partCraftMainCode, Integer status) {
        List<PartCraftDetail> list = new ArrayList<>();
        try {
            //获取款信息
            QueryWrapper<PartCraftDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(PartCraftDetail::getPartCraftMainRandomCode, partCraftMainCode)
                    .eq(SuperEntity::getStatus, status);

            list = detailDao.selectList(queryWrapper);
        } catch (Exception ex) {

        }
        return list;
    }

    @Override
    public List<PartCraftDetail> getPartCraftDetailRandomList(List<Long> randomCode) {
        List<PartCraftDetail> list = new ArrayList<>();
        try {
            list = detailDao.getPartCraftDetailRandomList(randomCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public List<PartCraftDetailVo> getPartCraftDetailVo(Long partCraftMainCode, Integer status) {
        List<PartCraftDetailVo> list = new ArrayList<>();
        try {
            list = detailDao.getPartCraftDetailVo(partCraftMainCode, status);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<Long, List<PartCraftDetailVo>> getPartCraftDetailMainRandomCodeByList(List<Long> randomCodes) {
        Map<Long, List<PartCraftDetailVo>> groupbyMap = new HashMap<>();
        try {
            if (null != randomCodes && randomCodes.size() > 0) {
                List<PartCraftDetailVo> list = detailDao.getPartCraftDetailBatchList(randomCodes);
                if (ObjectUtils.isNotEmptyList(list)) {
                    groupbyMap = list.stream().collect(Collectors.groupingBy(PartCraftDetailVo::getPartCraftMainRandomCode));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupbyMap;
    }
}