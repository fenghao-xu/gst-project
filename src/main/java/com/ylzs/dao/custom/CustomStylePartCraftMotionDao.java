package com.ylzs.dao.custom;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.custom.CustomStylePartCraftMotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定制款工艺工序动作，此表为定制款部件工序子表
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
@Mapper
public interface CustomStylePartCraftMotionDao extends BaseDAO<CustomStylePartCraftMotion> {


    public List<CustomStylePartCraftMotion> getCraftRandomCodeMotionList(@Param("arrays")List<Long> randomCodes);

    public List<CustomStylePartCraftMotion> getCraftRandomCodeMotionListOne(Long partCraftRandomCode);

    public int deleteCustomStyleMotionList(@Param("arrays")List<Long> randomCodes);
}
