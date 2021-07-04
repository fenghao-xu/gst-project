package com.ylzs.dao.partCraft;


import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCraft.PartCraftMasterData;
import com.ylzs.vo.partCraft.PartCraftCraftFlowVO;
import com.ylzs.vo.partCraft.PartCraftMasterBasicVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * 部件工艺主数据表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Mapper
public interface PartCraftMasterDataDao extends BaseDAO<PartCraftMasterData> {

    @Select("SELECT * FROM part_craft_master_data WHERE random_code = #{randomCode}")
    public PartCraftMasterData getPartCraftMasterData(Long randomCode);

    public List<PartCraftMasterBasicVo> searchPartCraftInfo(HashMap param);

    public List<PartCraftMasterBasicVo> searchPartCraftAndDetailInfo(HashMap param);

    public List<PartCraftMasterBasicVo> searchOnlyPartCraftInfo(HashMap param);


    public List<Long> getPartCraftMasterRandomCode(@Param("designCode") String designCode,
                                                   @Param("partPostion") String partPostion,
                                                   @Param("clothesCategoryCode") String clothesCategoryCode,
                                                   @Param("businessType") String businessType
    );

    public PartCraftMasterBasicVo searchPartCraftInfoRandomCode(HashMap param);

    public List<String> getPartCraftCodeNameAll();

    public int updatePaartCraftMaster(PartCraftMasterData masterData);

    public boolean isThinkStyleUsed(@Param("randomCode") Long randomCode);

    boolean isDelDesignPartUsed(@Param("masterRandomCode")  Long masterRandomCode,
                                @Param("designCodes")  List<String> designCodes,
                                @Param("positionCodes") List<String> positionCodes);

    public void updatePriceAndTime(PartCraftCraftFlowVO vo);

    boolean isPartCraftNameUsed(@Param("masterRandomCode") Long masterRandomCode,
                                @Param("craftCategoryCode") String craftCategoryCode,
                                @Param("partCraftName") String partCraftName);

    Integer updateStatusByDesignPartCode(@Param("status") Integer status, @Param("designPartCode") String designPartCode);
}
