package com.ylzs.service.custom;


import com.ylzs.entity.custom.CustomStylePart;
import com.ylzs.service.IOriginService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定制款部件信息
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
public interface ICustomStylePartService extends IOriginService<CustomStylePart> {

    public List<CustomStylePart> getMainRandomCodePartList(Long mainRandomCode);

    public List<CustomStylePart> getDataByMainRandomList(List<Long> randomList);

    public List<Long> getCustomStyleRandomCodeByMainRnadomCode(Long mainRandomCode);

    int deleteCustomStylePart(Long mainRandomCode);

    int deleteBatchCustomStylePart(List<Long> randomCodeList);
}

