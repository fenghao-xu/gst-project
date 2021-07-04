package com.ylzs.dao.thinkstyle;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.thinkstyle.ThinkStyleCraft;
import com.ylzs.vo.thinkstyle.CustomFlowNumVo;
import com.ylzs.vo.thinkstyle.ThinkStyleCraftVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 智库款工艺工序表
 * @author: lyq
 * @date: 2020-03-05 18:30
 */
@Mapper
public interface ThinkStyleCraftDao extends BaseDAO<ThinkStyleCraft> {
    List<ThinkStyleCraftVo> getSpecialCraftVos(@Param("partRandomCode") Long partRandomCode);

    List<CustomFlowNumVo> getCustomFlowNum(@Param("clothesCategoryCode") String clothesCategoryCode,
                                           @Param("craftCodes") String[] craftCodes,
                                           @Param("frameType") String frameType);

}
