package com.ylzs.dao.partCraft;


import com.ylzs.common.util.pageHelp.PageUtils;
import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCraft.PartCraftDetail;
import com.ylzs.vo.partCraft.PartCraftCraftFlowVO;
import com.ylzs.vo.partCraft.PartCraftDetailVo;
import com.ylzs.vo.thinkstyle.ThinkStyleCraftVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 部件工艺明细
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Mapper
public interface PartCraftDetailDao extends BaseDAO<PartCraftDetail> {

    @Select("SELECT * FROM part_craft_detail WHERE part_craft_main_random_code = #{partCraftMainCode}")
    List<PartCraftDetail> getPartCraftDetailMainList(Long partCraftMainCode);


    List<ThinkStyleCraftVo> getThinkStyleCraftVos(@Param("partCraftMainRandomCode") Long partCraftMainRandomCode);

    @Select("SELECT * FROM part_craft_detail WHERE part_craft_main_random_code = #{partCraftMainCode} AND status = #{status}")
    List<PartCraftDetail> getPartCraftDetail(Long partCraftMainCode, Integer status);

    List<PartCraftDetail> getPartCraftDetailRandomList(@Param("randomCodes") List<Long> randomCode);

    @Select("SELECT * FROM part_craft_detail WHERE part_craft_main_random_code = #{partCraftMainCode} AND status = #{status}")
    List<PartCraftDetailVo> getPartCraftDetailVo(Long partCraftMainCode, Integer status);

    List<PartCraftDetailVo> getPartCraftDetailBatchList(@Param("mainRandomCodes") List<Long> randomCode);

    public void updateCraftRemarkAndName(Map<String, Object> param);

    public void updateCraftFlowNum(Map<String, Object> param);

    public List<PartCraftCraftFlowVO> getNeededChangedData(@Param("craftCode") String craftCode);

}
