package com.ylzs.dao.thinkstyle;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.thinkstyle.ThinkStyleCraftHistory;
import com.ylzs.vo.thinkstyle.ThinkStyleCraftHistoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 智库款工艺工序历史表
 */
@Mapper
public interface ThinkStyleCraftHistoryDao extends BaseDAO<ThinkStyleCraftHistory> {
    List<ThinkStyleCraftHistoryVo> getThinkStyleCraftHistoryVos(@Param("partRandomCode") Long partRandomCode);
}