package com.ylzs.controller.factory;

import com.ylzs.web.OriginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.ylzs.service.factory.IProductionGroupService;



/**
 * 生产组别
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-05-05 09:13:31
 */
@RestController
public class ProductionGroupController extends OriginController {

    @Autowired
    private IProductionGroupService productionGroupService;


}
