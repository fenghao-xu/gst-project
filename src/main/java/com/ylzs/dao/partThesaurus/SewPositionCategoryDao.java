package com.ylzs.dao.partThesaurus;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylzs.entity.partThesaurus.SewPositionCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 缝边位置与品类关系数据持久层
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:36:52
 */
@Mapper
public interface SewPositionCategoryDao extends BaseMapper<SewPositionCategory> {
	
}
