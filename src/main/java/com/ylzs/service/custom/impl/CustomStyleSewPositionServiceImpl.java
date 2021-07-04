package com.ylzs.service.custom.impl;

import com.ylzs.dao.custom.CustomStyleSewPositionDao;
import com.ylzs.entity.custom.CustomStyleSewPosition;
import com.ylzs.service.custom.ICustomStyleSewPositionService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service()
public class CustomStyleSewPositionServiceImpl extends OriginServiceImpl<CustomStyleSewPositionDao, CustomStyleSewPosition> implements ICustomStyleSewPositionService {


    @Autowired
    private CustomStyleSewPositionDao craftPositionDao;

    @Override
    public List<CustomStyleSewPosition> getPartRandomCodeSewPositionList(Long mainRandomCode) {
        List<CustomStyleSewPosition> list = new ArrayList<>();
        try {
            list = craftPositionDao.getPartRandomCodeSewPositionList(mainRandomCode);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int deleteCustomStylePosition(Long mainRandomCode) {
        return craftPositionDao.deleteCustomStylePosition(mainRandomCode);
    }

    @Override
    public int deleteBatchCustomStylePosition(List<Long> randomCodeList) {
        return craftPositionDao.deleteBatchCustomStylePosition(randomCodeList);
    }
}