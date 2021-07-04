package com.ylzs.controller.custom;

import com.ylzs.service.custom.ICustomStylePartCraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;



/**
 * 定制款部件工序
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:05:41
 */
@RestController
public class CustomStylePartCraftController {

    @Autowired
    private ICustomStylePartCraftService customStylePartCraftService;


}
