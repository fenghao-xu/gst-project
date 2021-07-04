package com.ylzs.dao.thinkstyle;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.vo.thinkstyle.ThinkStylePartStandardVo;
import com.ylzs.vo.thinkstyle.ThinkStylePartVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @description: 智库款工艺部件表
 * @author: lyq
 * @date: 2020-03-05 18:33
 */
@Mapper
public interface ThinkStylePartDao extends BaseDAO<ThinkStylePart> {
    List<ThinkStylePartVo> selectThinkStylePartVo(@Param("styleRandomCode") Long styleRandomCode, @Param("clothingCategoryCode") String clothingCategoryCode);
    List<ThinkStylePartStandardVo> selectThinkStylePartStandardVos(Long styleRandomCode);
    @MapKey("randomCode")
    Map<Long,ThinkStylePart> selectThinkStylePart(Long styleRandomCode);
    @MapKey("designPartCode")
    Map<String, ThinkStylePart> getThinkStylePartMap(String styleCode);


}
