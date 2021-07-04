package com.ylzs.service.craftstd.impl;

import com.ylzs.common.util.PinyinUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.dao.datadictionary.DictionaryTypeDao;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.datadictionary.DictionaryType;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.vo.DictionaryVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 说明：字典服务实现
 *
 * @author Administrator
 * 2019-09-30 14:22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DictionaryService implements IDictionaryService {
    @Resource
    private DictionaryDao dictionaryDao;
    @Resource
    private DictionaryTypeDao dictionaryTypeDao;

    @Override
    public List<Dictionary> getDictionaryByTypeCode(String dictoryTypeCode) {
        return dictionaryDao.getDictionaryByTypeCode(dictoryTypeCode);
    }

    @Override
    public Map<String, Dictionary> getValueAndTypeCodeAsMapKey(List<String> typeCodeList) {
        return dictionaryDao.getValueAndTypeCodeAsMapKey(typeCodeList);
    }

    @Override
    public Integer addDictionary(Dictionary dictionary) {
        dictionary.setPyHeadChar(PinyinUtil.getAllFirstLetter(dictionary.getValueDesc()));
        //替换掉parentId 改成按类型保存
        DictionaryType dictionaryType = dictionaryTypeDao.getDictionaryTypeById(dictionary.getParentId());
        dictionary.setDictionaryTypeCode(dictionaryType.getDictionaryTypeCode());
        dictionary.setParentId(null);
        return dictionaryDao.addDictionary(dictionary);
    }

    @Override
    public Integer deleteDictionary(int id) {
        return dictionaryDao.deleteDictionary(id);
    }

    @Override
    public Integer updateDictionary(Dictionary dictionary) {
        if (dictionary.getValueDesc() != null && dictionary.getValueDesc() != "") {
            dictionary.setPyHeadChar(PinyinUtil.getAllFirstLetter(dictionary.getValueDesc()));
        }
        return dictionaryDao.updateDictionary(dictionary);
    }

    @Override
    public List<Dictionary> getDictionaryById(Integer id) {
        return dictionaryDao.getDictionaryById(id);
    }

    @Override
    public List<Dictionary> getDictionaryByParent(String parentCode, Integer parentId) {
        return  dictionaryDao.getDictionaryByTypeCode(parentCode);
        //return dictionaryDao.getDictionaryByParent(parentCode, parentId);
    }

    @Override
    public List<Dictionary> getDictionaryByPage(Integer parentId, String keywords, Date beginDate, Date endDate) {
        if(parentId == null) {
            //顶层取字典类型
            Map<String,Object> map = new HashMap<>();
            map.put("keywords",keywords);
            map.put("beginDate",beginDate);
            map.put("endDate",endDate);
            List<DictionaryType> dictionaryTypes = dictionaryTypeDao.getDictionaryTypePage(map);
            List<Dictionary> dicList = new ArrayList<>();
            if(dictionaryTypes != null && !dictionaryTypes.isEmpty()) {
                dictionaryTypes.forEach(x->{
                    Dictionary dic = new Dictionary();
                    dic.setId(x.getId().intValue());
                    dic.setDictionaryTypeCode(x.getDictionaryTypeCode());
                    dic.setDicValue(x.getDictionaryTypeCode());
                    dic.setValueDesc(x.getDictionaryTypeName());
                    dicList.add(dic);
                });
            }
            return dicList;
        }
        return dictionaryDao.getDictionaryByPage(parentId, keywords, beginDate, endDate);
    }

    @Override
    public List<Dictionary> getDictionaryByParentId(Integer parentId) {
        if(parentId != null) {
            DictionaryType dictionaryType = dictionaryTypeDao.getDictionaryTypeById(parentId);
            if(dictionaryType != null) {
                return  dictionaryDao.getDictionaryByTypeCode(dictionaryType.getDictionaryTypeCode());
            }
            return null;
        } else {
            return dictionaryDao.getDictionaryByParentId(parentId);
        }
    }

    @Override
    public List<Dictionary> getLineDictionary(Integer linePositionId, Integer lineNameId) throws Exception {
        return dictionaryDao.getLineDictionary(linePositionId, lineNameId);

    }

    @Override
    public List<DictionaryVo> getDictoryAll(String dictoryTypeCode) {
        List<DictionaryVo> dictionaryVos = new ArrayList<>();
        try {
            List<Dictionary> list = dictionaryDao.getDictoryAll(dictoryTypeCode);
            if(list.isEmpty()){
                throw new RuntimeException();
            }
            for(Dictionary d: list){
                DictionaryVo dictionaryVo = new DictionaryVo();
                dictionaryVo.setId(Integer.parseInt(String.valueOf(d.getId())));
                dictionaryVo.setDicValue(d.getDicValue());
                dictionaryVo.setValueDesc(d.getValueDesc());
                dictionaryVo.setRemark(d.getRemark());
                dictionaryVos.add(dictionaryVo);
            }
        }catch (Exception ex){
            new ArrayList<>();
            ex.printStackTrace();
        }
        return dictionaryVos;
    }


    @Override
    public List<DictionaryVo> getDictoryValueList(String kindCode, String valueDesc) {
        Map map = new HashMap();
        map.put("kindCode",kindCode);
        map.put("valueDesc",valueDesc);
        return dictionaryDao.getDictoryValueList(map);
    }

    @Override
    public List<Dictionary> getAll() {
        return dictionaryDao.getAll();
    }

    public List<Dictionary> getNoParentCode(){
        return dictionaryDao.getNoParentCode();
    }

    @Override
    public void addDictionaryList(List<Dictionary> dictionaryList) {
        dictionaryDao.addDictionaryList(dictionaryList);
    }

    @Override
    public void updateDictionaryList(List<Dictionary> dictionaryList) {
        dictionaryDao.updateDictionaryList(dictionaryList);
    }

    public void updateAndAddDictionary(List<Dictionary> dictionaryList){
        dictionaryDao.updateAndAddDictionary(dictionaryList);
    }


    public List<Dictionary> getDictoryByValueAndType(String dicValue, String dictionaryTypeCode) {
        return dictionaryDao.getDictoryByValueAndType(dicValue, dictionaryTypeCode);

    }

    @Override
    public String getBrandName(String brandCode) {
        String name = "";
        try {
            name = dictionaryDao.getBrandName(brandCode);
            if(StringUtils.isEmpty(name)){
                name = brandCode;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            name = brandCode;
        }
        return name;
    }
}
