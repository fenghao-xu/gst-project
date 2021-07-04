package com.ylzs.service.fms.impl;

import com.ylzs.dao.fms.StyleProductionWorkInformationDao;
import com.ylzs.entity.fms.StyleProductionWorkInformation;
import com.ylzs.service.fms.IStyleProductionWorkInformationService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @className StyleProductionWorkInformationService
 * @Description
 * @Author sky
 * @create 2020-04-24 10:16:55
 */
@Service
public class StyleProductionWorkInformationService extends OriginServiceImpl<StyleProductionWorkInformationDao,
        StyleProductionWorkInformation>implements IStyleProductionWorkInformationService {

    @Autowired
    private StyleProductionWorkInformationDao workInformationDao;
}
