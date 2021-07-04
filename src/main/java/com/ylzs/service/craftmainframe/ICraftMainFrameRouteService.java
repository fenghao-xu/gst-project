package com.ylzs.service.craftmainframe;

import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;

import java.util.Date;
import java.util.List;

/**
 * @description: 工艺主框架路线图接口
 * @author: lyq
 * @date: 2020-03-05 16:16
 */

public interface ICraftMainFrameRouteService {


    int deleteByPrimaryKey(Long randomCode);

    int insert(CraftMainFrameRoute record);

    int insertSelective(CraftMainFrameRoute record);


    CraftMainFrameRoute selectByPrimaryKey(Long randomCode);


    int updateByPrimaryKeySelective(CraftMainFrameRoute record);

    int updateByPrimaryKey(CraftMainFrameRoute record);

    int batchInsert(List<CraftMainFrameRoute> list);

    List<CraftMainFrameRoute> getByMainFrameRandomCode(Long mainFrameRandomCode);

    List<CraftMainFrameRoute> getMainFrameRouteByCode(String mainFrameCode);

    List<CraftMainFrameRoute> getByCondition(Long mainFrameRandomCode, String keywords, Date beginDate, Date endDate);
}
