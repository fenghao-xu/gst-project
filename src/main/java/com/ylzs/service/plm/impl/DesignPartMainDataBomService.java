package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.DesignPartDataBomDao;
import com.ylzs.dao.plm.DesignPartDataDao;
import com.ylzs.entity.plm.DesignPartMasterDataBom;
import com.ylzs.service.plm.IDesignPartMainDataBomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @Author:tt
* @Description：
* @Date: Created in 2020/3/12
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class DesignPartMainDataBomService implements IDesignPartMainDataBomService {

    @Resource
    DesignPartDataBomDao designPartDataBomDao;


    @Override
    public int addOrUpdatePartDataBom(List<DesignPartMasterDataBom> designPartMasterDataBomList) {
        return designPartDataBomDao.addOrUpdatePartDataBom(designPartMasterDataBomList);
    }
}
