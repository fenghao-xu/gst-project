package com.ylzs.controller.custom;

import com.ylzs.service.custom.ICustomStylePartCraftMotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;



/**
 * 定制款工艺工序动作，此表为定制款部件工序子表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:05:41
 */
@RestController
public class CustomStylePartCraftMotionController {

    @Autowired
    private ICustomStylePartCraftMotionService customStylePartCraftMotionService;


}
