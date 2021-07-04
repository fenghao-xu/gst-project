package com.ylzs.service.custom;


import com.ylzs.entity.custom.CustomStyleSewPosition;
import com.ylzs.service.IOriginService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工序词库部件关系
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-04-02 14:53:33
 */
public interface ICustomStyleSewPositionService extends IOriginService<CustomStyleSewPosition> {


    public List<CustomStyleSewPosition> getPartRandomCodeSewPositionList(Long mainRandomCode);

    int deleteCustomStylePosition(Long mainRandomCode);

    int deleteBatchCustomStylePosition(List<Long> randomCodeList);
}

