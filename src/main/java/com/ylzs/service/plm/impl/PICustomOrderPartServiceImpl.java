package com.ylzs.service.plm.impl;

import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.plm.PICustomOrderPartDao;
import com.ylzs.entity.plm.PICustomOrderPart;
import com.ylzs.entity.plm.PICustomOrderPartMaterial;
import com.ylzs.service.plm.IPICustomOrderPartMaterialService;
import com.ylzs.service.plm.IPICustomOrderPartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:31 2020/3/19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PICustomOrderPartServiceImpl implements IPICustomOrderPartService {


    @Resource
    private PICustomOrderPartDao piCustomOrderPartDao;
    @Resource
    private IPICustomOrderPartMaterialService partMaterialService;

    @Override
    public void addCustomOrderPartList(List<PICustomOrderPart> piCustomOrderParList) {
        piCustomOrderPartDao.addCustomOrderPartList(piCustomOrderParList);
    }

    @Override
    public List<PICustomOrderPart> getOrderAll(String orderId, String orderLineId) {
        List<PICustomOrderPart> partList = new ArrayList<>();
        try {
            partList = piCustomOrderPartDao.getOrderAll(orderId, orderLineId);
            for (PICustomOrderPart part : partList) {
                List<PICustomOrderPartMaterial> materialList = partMaterialService.getOrderAll(part.getOrderId(), part.getOrderLineId());
                if (ObjectUtils.isNotEmptyList(materialList)) {
                    Map<Long, List<PICustomOrderPartMaterial>> groupByMaterialList = materialList.stream()
                            .collect(Collectors.groupingBy(PICustomOrderPartMaterial::getParentRandomCode));
                    for (Long parentRandomCode : groupByMaterialList.keySet()) {
                        if (parentRandomCode.equals(part.getRandomCode())) {
                            part.setUms(groupByMaterialList.get(parentRandomCode));
                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return partList;
    }
}
