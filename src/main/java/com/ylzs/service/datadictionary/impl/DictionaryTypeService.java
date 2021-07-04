package com.ylzs.service.datadictionary.impl;

import com.ylzs.dao.datadictionary.DictionaryTypeDao;
import com.ylzs.entity.datadictionary.DictionaryType;
import com.ylzs.service.datadictionary.IDictionaryTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:26 2020/2/27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DictionaryTypeService implements IDictionaryTypeService {

    @Resource
    private DictionaryTypeDao dictionaryTypeDao;

    @Override
    public Integer addDictionaryType(DictionaryType dictionaryType) {
        return dictionaryTypeDao.addDictionaryType(dictionaryType);
    }

    @Override
    public Integer deleteDictionaryType(long id) {
        return dictionaryTypeDao.deleteDictionaryType(id);
    }

    @Override
    public Integer deleteDictionaryTypeByCode(List<DictionaryType> DictionaryTypes,String userCode) {

        return dictionaryTypeDao.deleteDictionaryTypeByCode(DictionaryTypes);
    }


    @Override
    public Integer updateDictionaryType(DictionaryType dictionaryType) {
        return dictionaryTypeDao.updateDictionaryType(dictionaryType);
    }

    @Override
    public List<DictionaryType> getDictionaryTypeByPage(String keywords, Date beginDate, Date endDate) {
        Map<String,Object> map = new HashMap<>();
        map.put("keywords",keywords);
        map.put("beginDate",beginDate);
        map.put("endDate",endDate);
        List<DictionaryType>data = dictionaryTypeDao.getDictionaryTypePage(map);
        return data;

    }

    @Override
    public DictionaryType getDictionaryTypeById(long id) {
        return dictionaryTypeDao.getDictionaryTypeById(id);
    }

    @Override
    public List<DictionaryType> getDictionaryTypeByCode(List<String> codes) {
        return dictionaryTypeDao.getDictionaryTypeByCode(codes);
    }

    @Override
    public DictionaryType getOneBydictionaryTypeCode(String dictionaryTypeCode) {
        return dictionaryTypeDao.getOneBydictionaryTypeCode(dictionaryTypeCode);
    }

    @Override
    public List<DictionaryType> getAll() {
        return dictionaryTypeDao.getAll();
    }


}
