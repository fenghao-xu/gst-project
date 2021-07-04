package com.ylzs.service.custom.impl;

import com.ylzs.dao.custom.CustomStylePartCraftDao;
import com.ylzs.entity.custom.CustomStylePartCraft;
import com.ylzs.service.custom.ICustomStylePartCraftService;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.vo.bigstylereport.WorkTypeReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service()
public class CustomStylePartCraftServiceImpl extends OriginServiceImpl<CustomStylePartCraftDao, CustomStylePartCraft> implements ICustomStylePartCraftService {

    @Autowired
    private CustomStylePartCraftDao customStylePartCraftDao;

    @Override
    public List<WorkTypeReport> selectWorkTypeReport(Map<String, Object> param) {
        return customStylePartCraftDao.selectWorkTypeReport(param);
    }

    @Override
    public List<CustomStylePartCraft> getPartRandomCodeCraftList(List<Long> randomCodeList) {
        List<CustomStylePartCraft> list = new ArrayList<>();
        try {
            list = customStylePartCraftDao.getPartRandomCodeCraftList(randomCodeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<CustomStylePartCraft> getPartRandomCodeCraftList(Long partRandomCode) {
        List<CustomStylePartCraft> list = new ArrayList<>();
        try {
            list = customStylePartCraftDao.getPartRandomCodeCraftListOne(partRandomCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int deleteCustomStyleCraftList(List<Long> randomCodeList) {
        return customStylePartCraftDao.deleteCustomStyleCraftList(randomCodeList);
    }
}