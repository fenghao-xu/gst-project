package com.ylzs.controller.partThesaurus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.ylzs.service.partThesaurus.IPartWarehousePositionService;



/**
 * 部件词库绣花、装饰位置
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:39
 */
@RestController
public class PartWarehousePositionController {

    @Autowired
    private IPartWarehousePositionService partWarehousePositionService;


}
