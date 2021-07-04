package com.ylzs.controller.partThesaurus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.ylzs.service.partThesaurus.IPartWarehouseService;



/**
 * 部件词库
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:39
 */
@RestController
public class PartWarehouseController {

    @Autowired
    private IPartWarehouseService partWarehouseService;


}
