package com.ylzs.dao.timeparam;

import com.ylzs.entity.staticdata.CraftGrade;
import com.ylzs.entity.staticdata.CraftGradeEquipment;
import com.ylzs.vo.craftstd.FactoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-02-27 11:38
 */
public interface CraftGradeEquipmentDao {
    /**
     * 得到所有工序等级
     */
    public List<CraftGradeEquipment> getAllCraftGrade();

    /**
     * 根据类型获取工序等级
     */
    public List<CraftGradeEquipment> getCraftGradeByType(String type);

    /**
     * 根据类型获取工序和等级编码获取
     */
    public CraftGradeEquipment getCraftGradeByTypeAndCode(@Param("type") String type, @Param("code") String code, @Param("factoryCode") String factoryCode);

    public List<CraftGradeEquipment> getCraftGradeFactoryData();

    public List<CraftGrade> getCraftGradeAll();

    public List<FactoryVO> getAllFactory();
}
