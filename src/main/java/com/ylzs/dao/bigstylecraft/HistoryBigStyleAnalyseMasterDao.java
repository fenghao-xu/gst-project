package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import org.apache.ibatis.annotations.Param;

public interface HistoryBigStyleAnalyseMasterDao {
    int deleteByPrimaryKey(Long id);

    int insert(BigStyleAnalyseMaster record);

    int insertSelective(BigStyleAnalyseMaster record);

    BigStyleAnalyseMaster selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BigStyleAnalyseMaster record);

    int updateByPrimaryKey(BigStyleAnalyseMaster record);

    BigStyleAnalyseMaster selectByStyleAnalyseCode(@Param("styleAnalyseCode") String styleAnalyseCode);
}