package com.ylzs.service.timeparam;

import com.ylzs.common.util.RedisUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.timeparam.CraftGradeEquipmentDao;
import com.ylzs.entity.staticdata.CraftGrade;
import com.ylzs.entity.staticdata.CraftGradeEquipment;
import com.ylzs.vo.craftstd.FactoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xuwei
 * @create 2020-02-27 11:43
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CraftGradeEquipmentService {
    @Resource
    CraftGradeEquipmentDao craftGradeEquipmentDao;
    private static final String CRAFT_GRADE_MENT_KEY = "craftGradeMent::";
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 得到所有工序等级
     */
    public List<CraftGradeEquipment> getAllCraftGrade() {
        return craftGradeEquipmentDao.getAllCraftGrade();
    }

    /**
     * 根据类型获取工序等级
     */
    public List<CraftGradeEquipment> getCraftGradeByType(String type) {
        return craftGradeEquipmentDao.getCraftGradeByType(type);
    }

    public CraftGradeEquipment getCraftGradeByTypeAndCode(String type, String code,String factoryCode) {
        return craftGradeEquipmentDao.getCraftGradeByTypeAndCode(type, code,factoryCode);
    }

    public List<CraftGradeEquipment> getCraftGradeFactoryData() {
        return craftGradeEquipmentDao.getCraftGradeFactoryData();
    }

    public List<FactoryVO> getAllFactory() {
        return craftGradeEquipmentDao.getAllFactory();
    }

    public List<CraftGrade> getCraftGradeAll() {
        List<CraftGrade> gradeList = craftGradeEquipmentDao.getCraftGradeAll();
        if (ObjectUtils.isNotEmptyList(gradeList)) {
            List<CraftGradeEquipment> equipmentList = getAllCraftGrade();
            if (ObjectUtils.isNotEmptyList(equipmentList)) {

                Map<String, List<CraftGradeEquipment>> groupByEuipmentList = equipmentList.stream().collect(Collectors.groupingBy(CraftGradeEquipment::getFactoryCode));
                gradeList.forEach(grade -> {
                    for (String factoryCode : groupByEuipmentList.keySet()) {
                        if (grade.getFactoryCode().equalsIgnoreCase(factoryCode)) {
                            grade.setCraftGradeEquipmentList(groupByEuipmentList.get(factoryCode));
                        }
                    }
                });
            }
        }
        return gradeList;
    }

    /**
     * 根据工厂编码获取工厂等级信息
     *
     * @param factoryCode    工厂编码
     * @param craftGradeCode 工序等级
     * @return
     */
    public CraftGradeEquipment getCraftGradeEquipment(String factoryCode, String craftGradeCode) {
        //   List<CraftGrade> craftGradeList = (List<CraftGrade>) redisUtil.get(CRAFT_GRADE_MENT_KEY);
        List<CraftGrade> craftGradeList = getCraftGradeAll();
        ;

//        if (ObjectUtils.isEmptyList(craftGradeList)) {
//            craftGradeList = getCraftGradeAll();
//            redisUtil.set(CRAFT_GRADE_MENT_KEY, craftGradeList, 86400);
//        }
        CraftGrade craftGradeMas = null;
        for (CraftGrade craftGrade : craftGradeList) {
            if (StringUtils.isBlank(factoryCode)) {
                if (craftGrade.getFactoryCode().equalsIgnoreCase("9999")) {
                    craftGradeMas = craftGrade;
                    break;
                }
            } else if (craftGrade.getFactoryCode().equalsIgnoreCase(factoryCode)) {
                craftGradeMas = craftGrade;
                break;
            }
        }
        CraftGradeEquipment gradeEquipment = null;
        if (craftGradeMas != null) {
            for (CraftGradeEquipment ment : craftGradeMas.getCraftGradeEquipmentList()) {
                if (ment.getCraftGradeCode().equalsIgnoreCase(craftGradeCode)) {
                    gradeEquipment = ment;
                    break;
                }
            }
        }
        return gradeEquipment;
    }
}
