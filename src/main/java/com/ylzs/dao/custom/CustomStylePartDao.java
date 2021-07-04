package com.ylzs.dao.custom;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.custom.CustomStylePart;
import com.ylzs.entity.designPart.DesignPart;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 定制款部件信息
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
@Mapper
public interface CustomStylePartDao extends BaseDAO<CustomStylePart> {

    public List<CustomStylePart> getMainRandomCodePartList(Long mainRandomCode);

    public List<CustomStylePart> getDataByMainRandomList(@Param("randomList") List<Long> randomList);

    public List<Long> getCustomStyleRandomCodeByMainRnadomCode(Long mainRandomCode);

    int deleteCustomStylePart(Long mainRandomCode);

    int deleteBatchCustomStylePart(@Param("arrays") List<Long> randomCodeList);

    @MapKey("randomCode")
    Map<Long, DesignPart> getCustomDesignPart(Long mainRandomCode);
}
