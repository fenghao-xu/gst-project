package com.ylzs.service.craftstd.impl;

import com.ylzs.common.util.PinyinUtil;
import com.ylzs.dao.craftstd.MakeTypeDao;
import com.ylzs.entity.craftstd.MakeType;
import com.ylzs.service.craftstd.IMakeTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：做工类型服务
 *
 * @author Administrator
 * 2019-09-30 11:12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MakeTypeService implements IMakeTypeService {
    @Resource
    private MakeTypeDao makeTypeDao;

    @Override
    public Integer addMakeType(MakeType makeType) {
        makeType.setPyHeadChar(PinyinUtil.getAllFirstLetter(makeType.getMakeTypeName()));
        return makeTypeDao.addMakeType(makeType);
    }

    @Override
    public Integer deleteMakeType(String makeTypeCode, String userCode) {
        Long count = makeTypeDao.getStdCountByMakeTypeCode(makeTypeCode);
        Integer ret = 0;
        if (count != null && count > 0) {
            MakeType makeType = new MakeType();
            makeType.setMakeTypeCode(makeTypeCode);
            makeType.setIsInvalid(true);
            makeType.setUpdateTime(new Date());
            makeType.setUpdateUser(userCode);
            ret = makeTypeDao.updateMakeType(makeType);
        } else {
            ret = makeTypeDao.deleteMakeType(makeTypeCode);
        }
        return ret;
    }

    @Override
    public Integer updateMakeType(MakeType makeType) {
        if (makeType.getMakeTypeName() != null && makeType.getMakeTypeName() != "") {
            makeType.setPyHeadChar(PinyinUtil.getAllFirstLetter(makeType.getMakeTypeName()));
        }
        return makeTypeDao.updateMakeType(makeType);
    }

    @Override
    public List<MakeType> getMakeTypeByCode(String[] makeTypeCodes) {
        return makeTypeDao.getMakeTypeByCode(makeTypeCodes);
    }

    @Override
    public List<MakeType> getMakeTypeByPage(String keywords, Date beginDate, Date endDate, Integer workTypeId) {
        return makeTypeDao.getMakeTypeByPage(keywords, beginDate, endDate, workTypeId);
    }

    @Override
    public MakeType getMakeTypeById(Integer id) {
        return makeTypeDao.getMakeTypeById(id);
    }

    @Override
    public List<MakeType> getAllMakeType() {
        return makeTypeDao.getAllMakeType();
    }

    @Override
    public Map<String, MakeType> getMakeTypeMap() {
        return makeTypeDao.getMakeTypeMap();
    }
}
