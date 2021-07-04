package com.ylzs.controller.timeparam;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.materialCraft.enums.StatusEnum;
import com.ylzs.entity.timeparam.StrappingTimeConfig;
import com.ylzs.entity.timeparam.StrappingTimeConfigReq;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.timeparam.StrappingTimeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.notBlank;
import static com.ylzs.common.util.Assert.notNull;

/**
 * @Author: watermelon.xzx
 * @Description:捆扎时间
 * @Date: Created in 10:59 2020/5/8
 */
@RestController
@RequestMapping("/strappingTimeConfig")
@Api(tags = "捆扎时间")
public class StrappingTimeConfigController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrappingTimeConfigController.class);

    @Resource
    private StrappingTimeConfigService strappingTimeConfigService;
    @Resource
    IOperationLogService operationLogService;


    @ApiOperation(value = "addStrappingTimeConfig", notes = "新增捆扎时间数据", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/addStrappingTimeConfig", method = RequestMethod.POST)
    public Result addStrappingTimeConfig(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                         @RequestBody StrappingTimeConfig strappingTimeConfig
    ) throws Exception {
        return strappingTimeConfigService.addStrappingTime(strappingTimeConfig, currentUser);
    }

    @ApiOperation(value = "updateStrappingTimeConfig", notes = "修改捆扎时间数据", httpMethod = "PUT", response = Result.class)
    @RequestMapping(value = "/updateStrappingTimeConfig", method = RequestMethod.PUT)
    public Result updateStrappingTimeConfig(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                            @RequestBody StrappingTimeConfig strappingTimeConfig) throws Exception {
        notBlank(strappingTimeConfig.getStrappingCode(), "捆扎时间编码不能为空");
        notBlank(strappingTimeConfig.getStrappingName(), "捆扎时间名称不能为空");
        notNull(strappingTimeConfig.getTime(), "捆扎时间不能为空");
        strappingTimeConfig.setUpdateUser(currentUser.getUser());
        strappingTimeConfig.setUpdateTime(new Date());
        return strappingTimeConfigService.updateStrappingTime(strappingTimeConfig);
    }

    @ApiOperation(value = "deleteStrappingTimeConfig", notes = "删除捆扎时间数据", httpMethod = "PUT", response = Result.class)
    @RequestMapping(value = "/deleteStrappingTimeConfig/{strappingCode}", method = RequestMethod.PUT)
    public Result deleteStrappingTimeConfig(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                            @PathVariable("strappingCode") String strappingCode) throws Exception {
        notBlank(strappingCode, "捆扎时间不能为空");
        String[] split = strappingCode.split(",");
        Integer ret = 0;
        strappingTimeConfigService.deleteStrappingTime(split, currentUser);
        for (int i = 0; i < split.length; i++) {
            operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                    currentUser.getUser(), "删除捆扎时间" + split[i]);
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getOne/{strappingCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询单个捆扎时间")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<StrappingTimeConfig>> getStrappingTimeConfigByCode(@PathVariable("strappingCode") String strappingCode) {
        notBlank(strappingCode, "做工类型代码不能为空");
        StrappingTimeConfig strappingTimeByCode = strappingTimeConfigService.getStrappingTimeByCode(strappingCode);
        List<StrappingTimeConfig> strappingTimeConfigList = new ArrayList<>();
        strappingTimeConfigList.add(strappingTimeByCode);
        return Result.ok(strappingTimeConfigList);
    }

    @RequestMapping(value = "/selectStrappingTimeConfig", method = RequestMethod.GET)
    @ApiOperation(value = "selectStrappingTimeConfig", notes = "查询捆扎时间数据")
    public Result<List<StrappingTimeConfig>> selectStrappingTimeConfig(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                                       @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                                       @RequestParam(name = "keywords", required = false) String keywords,
                                                                       @RequestParam(name = "beginDate", required = false) String beginDate,
                                                                       @RequestParam(name = "endDate", required = false) String endDate
    ) throws Exception{
        PageHelper.startPage(page, rows);
        DateTime newbeginDate = DateUtil.parse(beginDate);
        DateTime newendDate = DateUtil.parse(endDate);
        List<StrappingTimeConfig> strappingTimeConfigs = strappingTimeConfigService.getStrappingTimeByPage(keywords, newbeginDate, newendDate);
        PageInfo<StrappingTimeConfig> pageInfo = new PageInfo<>(strappingTimeConfigs);
        return Result.ok(strappingTimeConfigs, pageInfo.getTotal());
    }


    @ApiOperation(value = "exportStrappingTimeConfigList", notes = "导出捆扎时间数据清单", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportStrappingTimeConfigList", method = RequestMethod.GET)
    public Result exportStrappingTimeConfig(@RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                            @RequestParam(name = "keywords", required = false) String keywords,
                                            @RequestParam(name = "beginDate", required = false) String beginDate,
                                            @RequestParam(name = "endDate", required = false) String endDate,
                                            HttpServletResponse response) throws Exception {
        DateTime newbeginDate = DateUtil.parse(beginDate);
        DateTime newendDate = DateUtil.parse(endDate);
        List<StrappingTimeConfig> strappingTimeConfigs = strappingTimeConfigService.getStrappingTimeByPage(keywords, newbeginDate, newendDate);
        if (CollUtil.isEmpty(strappingTimeConfigs)) {
            return Result.ok("无数据导出");
        }
        List<StrappingTimeConfigReq> strappingTimeConfigReqList = new ArrayList<>(strappingTimeConfigs.size());
        for(StrappingTimeConfig strappingTimeConfig : strappingTimeConfigs){
            StrappingTimeConfigReq strappingTimeConfigReq = new StrappingTimeConfigReq();
            strappingTimeConfigReq.setStrappingCode(strappingTimeConfig.getStrappingCode());
            strappingTimeConfigReq.setStrappingName(strappingTimeConfig.getStrappingName());
            strappingTimeConfigReq.setTime(strappingTimeConfig.getTime());
            strappingTimeConfigReq.setRemark(strappingTimeConfig.getRemark());
            strappingTimeConfigReq.setStatus(StatusEnum.parse(strappingTimeConfig.getStatus()));
            strappingTimeConfigReq.setUpdateUser(strappingTimeConfig.getUpdateUser());
            strappingTimeConfigReq.setUpdateTime(strappingTimeConfig.getUpdateTime());
            strappingTimeConfigReqList.add(strappingTimeConfigReq);

        }
        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名
            writer.addHeaderAlias("strappingCode", "捆扎编码");
            writer.addHeaderAlias("strappingName", "捆扎名称");
            writer.addHeaderAlias("time", "时间");
            writer.addHeaderAlias("remark", "备注");
            writer.addHeaderAlias("status", "状态");
            writer.addHeaderAlias("updateUser", "维护人");
            writer.addHeaderAlias("updateTime", "维护时间");
            writer.write(strappingTimeConfigReqList, true);

            String fileName = URLEncoder.encode("捆扎时间数据清单.xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            LOGGER.error("exportMaterialCraftPageData fails", e);
        } finally {
            writer.close();
            if (null != out) {
                IoUtil.close(out);
            }
        }
        return null;
    }


    public String getModuleCode() {
        return "StrappingTimeConfig";
    }


}
