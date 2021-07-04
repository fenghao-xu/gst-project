package com.ylzs.service.plm.impl;

import com.ylzs.dao.plm.BigStyleMasterDataColorDao;
import com.ylzs.entity.plm.BigStyleMasterDataColor;
import com.ylzs.service.plm.IBigStyleMasterDataColorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:tt
 * @Description：大货主数据-color
 * @Date: Created in 2020/3/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BigStyleMasterDataColorService implements IBigStyleMasterDataColorService {

    @Resource
    BigStyleMasterDataColorDao bigStyleMasterDataColorDao;


    @Override
    public int addOrUpdateBigStyleDataColorList(List<BigStyleMasterDataColor> bigStyleMasterDataColor) {
        return bigStyleMasterDataColorDao.addOrUpdateBigStyleDataColorList(bigStyleMasterDataColor);
    }
}
