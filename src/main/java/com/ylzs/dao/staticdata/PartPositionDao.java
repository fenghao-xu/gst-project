package com.ylzs.dao.staticdata;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.craftstd.PartPosition;
import com.ylzs.vo.PartPositionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-04 15:23
 */
@Mapper
public interface PartPositionDao extends BaseDAO<PartPosition> {

    public List<PartPosition> getAllPartPositions();

    /**
     * @param clothingCategoryCode --服装品类编码
     * @return
     */
    public List<PartPosition> getAllPartPositionByCategory(String clothingCategoryCode);

    /**
     * 根据指定类型查询部件位置数据
     *
     * @param map
     * @return
     */
    public List<PartPositionVo> getPartPositonDataList(HashMap map);

    /**
     * 获取所有的缝边位置,或者根据服装品类获取缝边位置
     */
    public List<PartPosition> getSewingPartPositions(String param);


    List<PartPosition> getAll();

    //批量新增
    void addPartPosition(List<PartPosition> partPositionList);

    //批量更新
    void updatePartPosition(@Param("partPositionList") List<PartPosition> partPositionList);

    void addOrUpdatePartPosition(@Param("allPartPositionList") List<PartPosition> allPartPositionList);

    public List<PartPosition> getPartPositionByType(@Param("partType") String partType);

}
