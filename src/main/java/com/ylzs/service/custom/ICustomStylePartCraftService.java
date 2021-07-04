package com.ylzs.service.custom;


import com.ylzs.entity.custom.CustomStylePartCraft;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.bigstylereport.WorkTypeReport;

import java.util.List;
import java.util.Map;

/**
 * 定制款部件工序
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:01:58
 */
public interface ICustomStylePartCraftService extends IOriginService<CustomStylePartCraft> {


    public List<CustomStylePartCraft> getPartRandomCodeCraftList(List<Long> randomCodeList);

    public List<CustomStylePartCraft> getPartRandomCodeCraftList(Long partRandomCode);

    int deleteCustomStyleCraftList(List<Long> randomCodeList);

    public List<WorkTypeReport> selectWorkTypeReport(Map<String, Object> param);
}

