package com.ylzs.controller.partThesaurus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.ylzs.service.partThesaurus.IPartWarehouseFabricProcessService;



/**
 * 部件词库面料工艺
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:01:38
 */
@RestController
public class PartWarehouseFabricProcessController {

    @Autowired
    private IPartWarehouseFabricProcessService partWarehouseFabricProcessService;


}
