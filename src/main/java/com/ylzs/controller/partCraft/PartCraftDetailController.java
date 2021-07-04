package com.ylzs.controller.partCraft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.ylzs.service.partCraft.IPartCraftDetailService;



/**
 * 部件工艺明细
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@RestController
public class PartCraftDetailController {

    @Autowired
    private IPartCraftDetailService partCraftDetailService;


}
