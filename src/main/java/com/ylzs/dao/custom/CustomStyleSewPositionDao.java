package com.ylzs.dao.custom;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.custom.CustomStyleSewPosition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工序词库部件关系
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-04-02 14:53:33
 */
@Mapper
public interface CustomStyleSewPositionDao extends BaseDAO<CustomStyleSewPosition> {

    public List<CustomStyleSewPosition> getPartRandomCodeSewPositionList(Long mainRandomCode);

    int deleteCustomStylePosition(Long mainRandomCode);

    int deleteBatchCustomStylePosition(@Param("arrays") List<Long> randomCodeList);

}
