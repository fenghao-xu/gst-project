package com.ylzs.service.craftstd.impl;

import com.ylzs.dao.craftstd.CraftCategoryDao;
import com.ylzs.entity.craftstd.CategoryRelation;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.entity.craftstd.CraftPart;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.vo.CraftCateGoryVo;
import com.ylzs.vo.CraftPartVo;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.clothes.ClothesVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：工艺品类服务实现
 *
 * @author Administrator
 * 2019-09-23 16:43
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CraftCategoryService implements ICraftCategoryService {
    @Resource
    private CraftCategoryDao craftCategoryDao;

    @Resource
    private IDictionaryService dictionaryService;

    @Override
    public Integer addCraftCategory(CraftCategory craftCategory) {
        Integer retAdd = craftCategoryDao.addCraftCategory(craftCategory);
        if (retAdd != null && retAdd > 0 && craftCategory.getId() != null
                && craftCategory.getClothesCategoryIds() != null
                && !craftCategory.getClothesCategoryIds().isEmpty()) {
            String[] split = craftCategory.getClothesCategoryIds().split(",");
            List<DictionaryVo> dictionaryVos = dictionaryService.getDictoryAll("ClothesCategory");
            for (String clothesCategoryId : split) {
                int clothesCategoryNo = Integer.parseInt(clothesCategoryId);
                CategoryRelation categoryRelation = new CategoryRelation();
                categoryRelation.setCraftCategoryId(craftCategory.getId());
                categoryRelation.setClothesCategoryId(clothesCategoryNo);
                dictionaryVos.stream().filter(x->x.getId() == clothesCategoryNo).findFirst().ifPresent(ele->{
                    categoryRelation.setClothesCategoryCode(ele.getDicValue());
                });
                categoryRelation.setUpdateTime(new Date());
                categoryRelation.setUpdateUser(categoryRelation.getUpdateUser());
                categoryRelation.setCraftCategoryCode(craftCategory.getCraftCategoryCode());
                craftCategoryDao.addCategoryRelation(categoryRelation);
            }
        }
        return retAdd;
    }

    @Override
    public List<CraftCategory> getCraftCategoryClothesCategory() {
        return craftCategoryDao.getCraftCategoryClothesCategory();
    }

    @Override
    public Integer addCraftCategories(List<CraftCategory> craftCategories) {
        return craftCategoryDao.addCraftCategories(craftCategories);
    }

    @Override
    public Integer deleteCraftCategory(String craftCategoryCode, String userCode) {
        CraftCategory craftCategory = new CraftCategory();
        craftCategory.setCraftCategoryCode(craftCategoryCode);
        craftCategory.setIsInvalid(true);
        craftCategory.setUpdateUser(userCode);
        craftCategory.setUpdateTime(new Date());
        Integer ret = craftCategoryDao.updateCraftCategory(craftCategory);
        return ret;
    }

    @Override
    public Integer updateCraftCategory(CraftCategory craftCategory) {
        Integer retUpdate = craftCategoryDao.updateCraftCategory(craftCategory);
        if (retUpdate != null && retUpdate > 0) {
            if(craftCategory.getClothesCategoryIds() != null) {
                String[] codeArr = new String[]{craftCategory.getCraftCategoryCode()};
                List<CraftCategory> craftCategories = craftCategoryDao.getCraftCategoryByCode(codeArr);
                if (!craftCategories.isEmpty()) {
                    CraftCategory category = craftCategories.get(0);
                    craftCategoryDao.deleteCategoryRelationByCraftCategoryId(category.getId());
                    if (!craftCategory.getClothesCategoryIds().isEmpty()) {
                        List<DictionaryVo> dictionaryVos = dictionaryService.getDictoryAll("ClothesCategory");
                        String[] split = craftCategory.getClothesCategoryIds().split(",");
                        for (String clothesCategoryId : split) {
                            int clothesCategoryNo = Integer.parseInt(clothesCategoryId);
                            CategoryRelation categoryRelation = new CategoryRelation();
                            categoryRelation.setCraftCategoryId(category.getId());
                            categoryRelation.setCraftCategoryCode(category.getCraftCategoryCode());
                            categoryRelation.setClothesCategoryId(clothesCategoryNo);
                            dictionaryVos.stream().filter(x->x.getId() == clothesCategoryNo).findFirst().ifPresent(ele->{
                                categoryRelation.setClothesCategoryCode(ele.getDicValue());
                            });
                            categoryRelation.setUpdateTime(new Date());
                            categoryRelation.setUpdateUser(craftCategory.getUpdateUser());
                            craftCategoryDao.addCategoryRelation(categoryRelation);
                        }
                    }
                }
            }
        }


        return retUpdate;
    }

    @Override
    public List<CraftCategory> getCraftCategoryByCode(String[] craftCategoryCodes) {
        return craftCategoryDao.getCraftCategoryByCode(craftCategoryCodes);
    }

    @Override
    public List<CraftCategory> getCraftCategoryByPage(String keywords, Date beginDate, Date endDate) {
        return craftCategoryDao.getCraftCategoryByPage(keywords, beginDate, endDate);
    }

    @Override
    public CraftCategory getCraftCategoryById(Integer id) {
        return craftCategoryDao.getCraftCategoryById(id);

    }


    @Override
    public Integer getMaxSeqNum() {
        return craftCategoryDao.getMaxSeqNum();
    }

    @Override
    public List<CraftCategory> getCraftCategoryAndPart() {
        return craftCategoryDao.getCraftCategoryAndPart();
    }

    @Override
    public List<CraftCategory> getCraftCategoryCodeAndName() {
        return craftCategoryDao.getCraftCategoryCodeAndName();
    }

    @Override
    public List<CraftCategory> getAllValidCraftCategory() {
        return craftCategoryDao.getAllValidCraftCategory();
    }

    @Override
    public List<CraftCateGoryVo> getCraftCategoryApartVos() {
        List<CraftCategory> craftCategoryList = getCraftCategoryAndPart();
        List<CraftCateGoryVo> voList = new ArrayList<>();
        if(craftCategoryList.isEmpty()){
            return voList;
        }
        for(CraftCategory category: craftCategoryList){
            CraftCateGoryVo vos = new CraftCateGoryVo();
            List<CraftPartVo> partVos = new ArrayList<>();
            vos.setId(category.getId());
            vos.setCraftCategoryCode(category.getCraftCategoryCode());
            vos.setCraftCategoryName(category.getCraftCategoryName());
            for(CraftPart craftPart: category.getCraftPartList()){
                CraftPartVo partVo = new CraftPartVo();
                partVo.setId(craftPart.getId());
                partVo.setCraftPartCode(craftPart.getCraftPartCode());
                partVo.setCraftPartName(craftPart.getCraftPartName());
                partVos.add(partVo);
            }
            vos.setCraftPartVos(partVos);
            voList.add(vos);
        }
        return voList;
    }

    @Override
    public List<ClothesVo> getClothesAll(Integer []ids) {
        List<ClothesVo> list = new ArrayList<>();
        try {
            list = craftCategoryDao.getClothesAll(ids);
        }catch (Exception ex){
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<Map<String, String>> getCraftCategoryAndClothesCategory() {
        return craftCategoryDao.getCraftCategoryAndClothesCategory();
    }

    @Override
    public String getCraftCategoryCode(String clotheCategoryCode) {
        String code = null;
        try {
            code = craftCategoryDao.getCraftCategoryCode(clotheCategoryCode);
        }catch (Exception e){

        }
        return code;
    }

    @Override
    public List<CraftCategory> getAll() {
        return craftCategoryDao.getAll();
    }
}
