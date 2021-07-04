package com.ylzs.controller.system;

import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.entity.system.WebConfig;
import com.ylzs.service.system.IWebConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xuwei
 * @create 2019-09-17 13:49
 */
@RestController
@RequestMapping("/webConfig")
@Api(tags = "web静态配置")
public class WebConfigController implements IModuleInfo {
    @Resource
    IWebConfigService webConfigService;

    @PostMapping(value = "/getAllConfig")
    @ApiOperation(value = "getAllConfig", notes = "测试getAllConfig请求notest")
    public WebConfig getTest(@RequestParam(name = "key", required = false) String key) {
        return  webConfigService.getConfigByKey(key);
    }

    @Override
    public String getModuleCode() {
        return "web-config";
    }

}
