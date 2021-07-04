package com.ylzs.service.custom.impl;

import com.ylzs.dao.custom.CustomStylePartDao;
import com.ylzs.entity.custom.CustomStylePart;
import com.ylzs.service.custom.ICustomStylePartService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service()
public class CustomStylePartServiceImpl extends OriginServiceImpl<CustomStylePartDao, CustomStylePart> implements ICustomStylePartService {

    @Autowired
    private CustomStylePartDao partDao;

    @Override
    public List<CustomStylePart> getMainRandomCodePartList(Long mainRandomCode) {
        return partDao.getMainRandomCodePartList(mainRandomCode);
    }

    @Override
    public List<CustomStylePart> getDataByMainRandomList(List<Long> randomList) {
        return partDao.getDataByMainRandomList(randomList);
    }

    @Override
    public int deleteBatchCustomStylePart(List<Long> randomCodeList) {
        return partDao.deleteBatchCustomStylePart(randomCodeList);
    }

    @Override
    public List<Long> getCustomStyleRandomCodeByMainRnadomCode(Long mainRandomCode) {
        return partDao.getCustomStyleRandomCodeByMainRnadomCode(mainRandomCode);
    }

    @Override
    public int deleteCustomStylePart(Long mainRandomCode) {
        return partDao.deleteCustomStylePart(mainRandomCode);
    }
}