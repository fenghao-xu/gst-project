package com.ylzs.service.plm.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.util.StringUtils;
import com.ylzs.dao.plm.PlmComponentMaterialDao;
import com.ylzs.dao.plm.PlmMasterDataComponentDao;
import com.ylzs.dao.plm.ProductModelMasterDataDao;
import com.ylzs.entity.plm.*;
import com.ylzs.service.plm.IProductModelMasterDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: watermelon.xzx
 * @Description: PI传智库款数据
 * @Date: Created in 14:02 2020/3/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProductModelMasterDataServiceImpl implements IProductModelMasterDataService{
    @Resource
    private ProductModelMasterDataDao productModelMasterDataDao;

    @Resource
    private PlmComponentMaterialDao plmComponentMaterialDao;
    @Resource
    private PlmMasterDataComponentDao plmMasterDataComponentDao;

    @Override
    public void addOrUpdateProductModelDataDao(ProductModelMasterData productModelMasterData) {
        //先更新款式主数据
        if(productModelMasterData.getLength()==null){
            productModelMasterData.setLength(new BigDecimal(0));
        }
        if(productModelMasterData.getGrandCategory().indexOf("-")!=-1){
            productModelMasterData.setGrandCategory(productModelMasterData.getGrandCategory().substring(0,productModelMasterData.getGrandCategory().indexOf("-")));
        }else{
            productModelMasterData.setGrandCategory(productModelMasterData.getGrandCategory());
        }
        if(productModelMasterData.getMidCategory().indexOf("-")!=-1){
            productModelMasterData.setMidCategory(productModelMasterData.getMidCategory().substring(0,productModelMasterData.getMidCategory().indexOf("-")));
        }else{
            productModelMasterData.setMidCategory(productModelMasterData.getMidCategory());
        }
        if(productModelMasterData.getSmallCategory().indexOf("-")!=-1){
            productModelMasterData.setSmallCategory(productModelMasterData.getSmallCategory().substring(0,productModelMasterData.getSmallCategory().indexOf("-")));
        }else{
            productModelMasterData.setSmallCategory(productModelMasterData.getSmallCategory());
        }
        productModelMasterData.setReceiveTime(new Date());
        productModelMasterDataDao.addOrUpdateProductModelDataDao(productModelMasterData);
        //再添加部件数据
        List<ProductModelEBomData> eBomDataList = productModelMasterData.getEBom();
        List<ProductModelTuAnDefaultPart> tuAnDefaultParts = productModelMasterData.getTuAnDefaultParts();
        List<String> styleCodeList = new ArrayList<>();
        //添加款式code ，先物理删除，然后再新增
        plmComponentMaterialDao.delPlmComponentMaterial(productModelMasterData.getCode());
        plmMasterDataComponentDao.delPlmMasterDataComponent(productModelMasterData.getCode());
        List<PlmComponentMaterial> PlmComponentMaterialList = new ArrayList<>();//部件材料
        List<PlmMasterDataComponent> PlmMasterdataComponentList = new ArrayList<>();//款式部件
        if(eBomDataList != null)
        {
            for(ProductModelEBomData productModelEBomData : eBomDataList){
                styleCodeList.add(productModelMasterData.getCode());
                if("1".equals(productModelEBomData.getIsComponent())){
                    if(StringUtils.isNotBlank(productModelEBomData.getDefaultComponent())||StringUtils.isNotBlank(productModelEBomData.getOptionalComponents())){
                            if(productModelEBomData.getDefaultComponent()!=null&&StringUtils.isNotBlank(productModelEBomData.getDefaultComponent())){
                                PlmMasterDataComponent plmMasterdataComponent = new PlmMasterDataComponent();
                                plmMasterdataComponent.setStyleCode(productModelMasterData.getCode());
                                plmMasterdataComponent.setComponentCode(productModelEBomData.getDefaultComponent());//部件编码，为默认和可选，有几条单独存几条
                                plmMasterdataComponent.setIsDefaultComponent(true);
                                if (StringUtils.isNotBlank(productModelEBomData.getMtmPositionCode())){
                                    plmMasterdataComponent.setPositionCode(productModelEBomData.getMtmPositionCode());
                                    //绣花或图案位置
                                    plmMasterdataComponent.setPositionType("embroider");
                                }else{
                                    if (StringUtils.isNotBlank(productModelEBomData.getEmbroideryPositionValue())) {
                                        plmMasterdataComponent.setPositionCode(productModelEBomData.getEmbroideryPositionValue());
                                        //缝边位置
                                        plmMasterdataComponent.setPositionType("sewing");
                                    }
                                }
                                plmMasterdataComponent.setParentComponentCode(productModelEBomData.getParentComponent());
                                plmMasterdataComponent.setTopPosition(productModelEBomData.getBelongToComponent());
                                PlmMasterdataComponentList.add(plmMasterdataComponent);
                            }
                            if (StringUtils.isNotBlank(productModelEBomData.getOptionalComponents())&&productModelEBomData.getOptionalComponents()!=null){
                                String[] split = productModelEBomData.getOptionalComponents().split(",");

                                for(int i=0;i<split.length;i++){
                                    PlmMasterDataComponent plmMasterdataComponent = new PlmMasterDataComponent();
                                    plmMasterdataComponent.setStyleCode(productModelMasterData.getCode());
                                    plmMasterdataComponent.setComponentCode(split[i]);
                                    plmMasterdataComponent.setIsDefaultComponent(false);
                                    if (StringUtils.isNotBlank(productModelEBomData.getMtmPositionCode())){
                                        plmMasterdataComponent.setPositionCode(productModelEBomData.getMtmPositionCode());
                                        //绣花或图案位置
                                        plmMasterdataComponent.setPositionType("embroider");
                                    }else{
                                        if (StringUtils.isNotBlank(productModelEBomData.getEmbroideryPositionValue())) {
                                            plmMasterdataComponent.setPositionCode(productModelEBomData.getEmbroideryPositionValue());
                                            //缝边位置
                                            plmMasterdataComponent.setPositionType("sewing");
                                        }
                                    }
                                    plmMasterdataComponent.setParentComponentCode(productModelEBomData.getParentComponent());
                                    plmMasterdataComponent.setTopPosition(productModelEBomData.getBelongToComponent());
                                    plmMasterdataComponent.setReceiveTime(new Date());
                                    PlmMasterdataComponentList.add(plmMasterdataComponent);
                                }
                            }

                    }
                }else if("0".equals(productModelEBomData.getIsComponent())&&"基础材料".equals(productModelEBomData.getComponentType())
                        &&productModelEBomData.getDefaultMaterial()!=null&&StringUtils.isNotBlank(productModelEBomData.getDefaultMaterial())){
                    PlmComponentMaterial plmComponentMaterial = new PlmComponentMaterial();
                    plmComponentMaterial.setStyleCode(productModelMasterData.getCode());
                    plmComponentMaterial.setComponentCode(productModelEBomData.getParentComponent());
                    plmComponentMaterial.setMaterialLargeType(productModelEBomData.getMaterialLargeType());
                    plmComponentMaterial.setMaterialMidType(productModelEBomData.getMaterialMidType());
                    plmComponentMaterial.setDefaultMaterial(productModelEBomData.getDefaultMaterial());
                    plmComponentMaterial.setBelongToComponentCode(productModelEBomData.getBelongToComponent());
                    plmComponentMaterial.setIsMainComponentOrMaterial(productModelEBomData.getIsMainComponentOrMaterial());
                    PlmComponentMaterialList.add(plmComponentMaterial);
                }
            }
        }
        if(tuAnDefaultParts != null) {
            List<ProductModelTuAnDefaultPart> tmpList = new ArrayList<>();
            for(ProductModelTuAnDefaultPart tuAnPart: tuAnDefaultParts) {
                String componentCode = tuAnPart.getComponentCode()==null?"":tuAnPart.getComponentCode();
                String positionCode = tuAnPart.getPosition()==null?"":tuAnPart.getPosition();
                ProductModelTuAnDefaultPart tmp = tmpList.stream().filter(
                        x->componentCode.equals(x.getComponentCode()) && positionCode.equals(x.getPosition()))
                        .findFirst().orElse(null);
                if(null != tmp) {
                    continue;
                }
                tmpList.add(tuAnPart);


                PlmMasterDataComponent plmMasterdataComponent = new PlmMasterDataComponent();
                plmMasterdataComponent.setStyleCode(productModelMasterData.getCode());
                plmMasterdataComponent.setComponentCode(tuAnPart.getComponentCode());
                plmMasterdataComponent.setIsDefaultComponent(tuAnPart.getIsDefaultComponent());
                if (StringUtils.isNotBlank(tuAnPart.getPosition())){
                    plmMasterdataComponent.setPositionCode(tuAnPart.getPosition());
                    plmMasterdataComponent.setPositionType("decoration");
                }
                plmMasterdataComponent.setParentComponentCode("");
                plmMasterdataComponent.setTopPosition("");
                plmMasterdataComponent.setReceiveTime(new Date());
                PlmMasterdataComponentList.add(plmMasterdataComponent);
            }
        }


        if(PlmMasterdataComponentList!=null&&PlmMasterdataComponentList.size()>0){
        plmMasterDataComponentDao.addPlmMasterDataComponent(PlmMasterdataComponentList);
        }
        if(PlmComponentMaterialList!=null&&PlmComponentMaterialList.size()>0){
        plmComponentMaterialDao.addPlmComponentMaterial(PlmComponentMaterialList);
        }

    }

    @Override
    public List<String> getAllStyleCode() {
        QueryWrapper<ProductModelMasterData> query = new QueryWrapper<>();
        //query.lambda().ge(ProductModelMasterData::getId, 2954);
        List<ProductModelMasterData> lst = productModelMasterDataDao.selectList(query);
        List<String> styleCodes = new ArrayList<>();
        for(ProductModelMasterData itm: lst) {
            styleCodes.add(itm.getCode());
        }
        return styleCodes;
    }
}
