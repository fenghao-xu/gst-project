package com.ylzs.dao.fms;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.fms.StyleProductionWorkInformation;

import java.util.List;

/**
 * @className StyleProductionWorkInformationDao
 * @Description
 * @Author sky
 * @create 2020-04-24 10:16:00
 */
public interface StyleProductionWorkInformationDao extends BaseDAO<StyleProductionWorkInformation> {
    public List<StyleProductionWorkInformation>getAllData();
}
