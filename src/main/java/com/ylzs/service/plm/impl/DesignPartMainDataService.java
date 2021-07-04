package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.DesignPartDataDao;
import com.ylzs.entity.plm.DesignPartMasterData;
import com.ylzs.service.plm.IDesignPartMainDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
/**
* @Author:tt
* @Description：
* @Date: Created in 2020/3/12
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class DesignPartMainDataService implements IDesignPartMainDataService {

    @Resource
    DesignPartDataDao designPartDataDao;



}
