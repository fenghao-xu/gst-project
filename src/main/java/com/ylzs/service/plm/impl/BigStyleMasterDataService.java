package com.ylzs.service.plm.impl;

import com.ylzs.dao.craftstd.CraftCategoryDao;
import com.ylzs.dao.plm.BigStyleMasterDataDao;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.plm.BigStyleMasterData;
import com.ylzs.service.plm.IBigStyleMasterDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author:tt
 * @Description：大货主数据
 * @Date: Created in 2020/3/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BigStyleMasterDataService implements IBigStyleMasterDataService {
    @Resource
    BigStyleMasterDataDao bigStyleMasterDataDao;

    @Resource
    private DictionaryDao dictionaryDao;

    @Resource
    private CraftCategoryDao craftCategoryDao;

    @Override
    public int addOrUpdateBigStyleData(BigStyleMasterData bigStyleMasterData) {
        return bigStyleMasterDataDao.addOrUpdateBigStyleData(bigStyleMasterData);
    }

    @Override
    public List<BigStyleMasterData> getDataByStyleRandomCode(String tableName, Long styleRandomCode) {
        return bigStyleMasterDataDao.getDataByStyleRandomCode(tableName,styleRandomCode);
    }

    @Override
    public List<BigStyleMasterData> getAllDataForPage(Map<String, Object> params) {
        List<String> list = new ArrayList<>();
        list.add("ClothesCategory");
        list.add("SubBrand");
        List<BigStyleMasterData> data = bigStyleMasterDataDao.getAllDataForPage(params);
        Map<String, Dictionary> map = dictionaryDao.getValueAndTypeCodeAsMapKey(list);
        if (null != data && data.size() > 0 && null != map && map.size() > 0) {
            data.stream().parallel().forEach(vo -> {
                //把面料等级的字段放入到面料分值，防止前端弄混
                vo.setFabricFraction(vo.getMaterialGrade());
                vo.setMaterialGrade(null);
                if (map.containsKey(vo.getSubBrand() + "_" + "SubBrand")) {
                    vo.setSubBrandName(map.get(vo.getSubBrand() + "_" + "SubBrand").getValueDesc());
                }
                if (map.containsKey(vo.getGrandCategory() + "_" + "ClothesCategory")) {
                    vo.setGrandCategoryName(map.get(vo.getGrandCategory() + "_" + "ClothesCategory").getValueDesc());
                }
                List<CraftCategory> categoryList = craftCategoryDao.getCraftCategoryByClothesCategoryCode(vo.getGrandCategory());
                if (null != categoryList && categoryList.size() > 0) {
                    vo.setCraftCategoryCode(categoryList.get(0).getCraftCategoryCode());
                    vo.setCraftCategoryName(categoryList.get(0).getCraftCategoryName());
                }
            });
        }
        return data;
    }
}
