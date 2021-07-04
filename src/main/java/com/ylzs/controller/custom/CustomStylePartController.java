package com.ylzs.controller.custom;

import com.ylzs.service.custom.ICustomStylePartService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;



/**
 * 定制款部件信息
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-03-18 11:05:41
 */
@Api(tags = "定制款订单工艺-部件web")
@RestController
public class CustomStylePartController {

    @Autowired
    private ICustomStylePartService customStylePartService;


}
