package com.ylzs.controller.custom;

import com.ylzs.service.custom.ICustomStyleRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;



/**
 * 定制款工艺路径规则表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:05:41
 */
@RestController
public class CustomStyleRuleController {

    @Autowired
    private ICustomStyleRuleService customStyleRuleService;


}
