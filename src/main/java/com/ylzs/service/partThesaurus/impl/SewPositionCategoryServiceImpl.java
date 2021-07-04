package com.ylzs.service.partThesaurus.impl;

import com.ylzs.dao.partThesaurus.SewPositionCategoryDao;
import com.ylzs.entity.partThesaurus.SewPositionCategory;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ylzs.service.partThesaurus.ISewPositionCategoryService;


@Service()
public class SewPositionCategoryServiceImpl extends OriginServiceImpl<SewPositionCategoryDao,SewPositionCategory> implements ISewPositionCategoryService {

    @Autowired
    private  SewPositionCategoryDao sewPositionCategoryDao;

}