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
import com.ylzs.entity.timeparam.WideCoefficient;
import com.ylzs.entity.timeparam.WideCoefficientReq;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.timeparam.StrappingTimeConfigService;
import com.ylzs.service.timeparam.WideCoefficientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.notBlank;
import static com.ylzs.common.util.Assert.notNull;

/**
 * @Author: watermelon.xzx
 * @Description:宽放系数 不要使用宽放系数里面的randomCode，使用wideCode关联
 * @Date: Created in 19:18 2020/5/13
 */
@RestController
@RequestMapping("/wideCoefficient")
@Api(tags = "宽放系数")
public class WideCoefficientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WideCoefficientController.class);

    @Resource
    private WideCoefficientService wideCoefficientService;
    @Resource
    IOperationLogService operationLogService;

    @ApiOperation(value = "addWideCoefficient", notes = "新增宽放系数数据", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/addWideCoefficient", method = RequestMethod.POST)
    public Result addWideCoefficient(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                    @RequestBody WideCoefficientReq wideCoefficientReq
    ) throws Exception {
        notNull(wideCoefficientReq.getWideCode(), "宽放编码不能为空");
        notNull(wideCoefficientReq.getWideName(), "宽放名称不能为空");
        notNull(wideCoefficientReq.getCoefficient(), "系数不能为空");
        notNull(wideCoefficientReq.getCraftCategoryCodes(), "工艺品类不能为空");
        return wideCoefficientService.addWideCoefficient(wideCoefficientReq, currentUser);
    }

    @ApiOperation(value = "updateWideCoefficient", notes = "修改宽泛系数数据", httpMethod = "PUT", response = Result.class)
    @RequestMapping(value = "/updateWideCoefficient", method = RequestMethod.PUT)
    public Result updateWideCoefficient(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                            @RequestBody WideCoefficientReq wideCoefficientreq) throws Exception {
        notBlank(wideCoefficientreq.getWideCode(), "宽放系数编码不能为空");
        notBlank(wideCoefficientreq.getWideName(), "宽放系数名称不能为空");
        notNull(wideCoefficientreq.getCraftCategoryCodes(), "工艺品类编码不能为空");
        notNull(wideCoefficientreq.getCoefficient(), "系数不能为空");
        wideCoefficientreq.setUpdateUser(currentUser.getUser());
        wideCoefficientreq.setUpdateTime(new Date());
        return wideCoefficientService.updateWideCoefficient(wideCoefficientreq);
    }

    @ApiOperation(value = "deleteWideCoefficient", notes = "删除宽放系数数据", httpMethod = "PUT", response = Result.class)
    @RequestMapping(value = "/deleteWideCoefficient/{wideCode}", method = RequestMethod.PUT)
    public Result deleteWideCoefficient(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                            @PathVariable("wideCode") String wideCode) throws Exception {
        notBlank(wideCode, "宽放系数编码不能为空");
        String[] split = wideCode.split(",");
        Integer ret = 0;
        wideCoefficientService.deleteWideCoefficiente(split, currentUser);
        for (int i = 0; i < split.length; i++) {
            operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                    currentUser.getUser(), "删除宽放系数" + split[i]);
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getOne/{wideCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询单个宽放系数")
    public Result<List<WideCoefficientReq>> getWideCoefficientByCode(@PathVariable("wideCode") String wideCode) {
        notBlank(wideCode, "宽放系数编码不能为空");
        WideCoefficientReq wideCoefficientReq  = wideCoefficientService.getWideCoefficientByWideCode(wideCode);

        List<WideCoefficientReq> wideCoefficientReqList = new ArrayList<>();
        wideCoefficientReqList.add(wideCoefficientReq);
        return Result.ok(wideCoefficientReqList);
    }

    @RequestMapping(value = "/selectWideCoefficient", method = RequestMethod.GET)
    @ApiOperation(value = "selectWideCoefficient", notes = "查询宽放系数数据")
    public Result<List<WideCoefficient>> selectWideCoefficient(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                                       @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                                       @RequestParam(name = "keywords", required = false) String keywords,
                                                                       @RequestParam(name = "beginDate", required = false) String beginDate,
                                                                       @RequestParam(name = "endDate", required = false) String endDate
    ) throws Exception{
        PageHelper.startPage(page, rows);
        DateTime newbeginDate = DateUtil.parse(beginDate);
        DateTime newendDate = DateUtil.parse(endDate);
        List<WideCoefficient> wideCoefficients = wideCoefficientService.getWideCoefficientByPage(keywords, newbeginDate, newendDate);
        PageInfo<WideCoefficient> pageInfo = new PageInfo<>(wideCoefficients);
        return Result.ok(wideCoefficients, pageInfo.getTotal());
    }


    @ApiOperation(value = "exportWideCoefficient", notes = "导出捆扎时间数据清单", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportWideCoefficientList", method = RequestMethod.GET)
    public Result exportWideCoefficientList(@RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                            @RequestParam(name = "keywords", required = false) String keywords,
                                            @RequestParam(name = "beginDate", required = false) String beginDate,
                                            @RequestParam(name = "endDate", required = false) String endDate,
                                            HttpServletResponse response) throws Exception {
        DateTime newbeginDate = DateUtil.parse(beginDate);
        DateTime newendDate = DateUtil.parse(endDate);
        List<WideCoefficient> wideCoefficients = wideCoefficientService.getWideCoefficientByPage(keywords, newbeginDate, newendDate);
        if (CollUtil.isEmpty(wideCoefficients)) {
            return Result.ok("无数据导出");
        }
        List<WideCoefficientReq> wideCoefficientRepList = new ArrayList<>(wideCoefficients.size());
        for(WideCoefficient wideCoefficient : wideCoefficients){
            WideCoefficientReq wideCoefficientReq = new WideCoefficientReq();
            wideCoefficientReq.setWideCode(wideCoefficient.getWideCode());
            wideCoefficientReq.setWideName(wideCoefficient.getWideName());
            wideCoefficientReq.setCoefficient(wideCoefficient.getCoefficient());
            wideCoefficientReq.setCraftCategoryCode(wideCoefficient.getCraftCategoryCode());
            wideCoefficientReq.setRemark(wideCoefficient.getRemark());
            wideCoefficientReq.setStatus(StatusEnum.parse(wideCoefficient.getStatus()));
            wideCoefficientReq.setUpdateUser(wideCoefficient.getUpdateUser());
            wideCoefficientReq.setUpdateTime(wideCoefficient.getUpdateTime());
            wideCoefficientRepList.add(wideCoefficientReq);

        }
        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名
            writer.addHeaderAlias("wideCode", "宽放编码");
            writer.addHeaderAlias("wideName", "宽放名称");
            writer.addHeaderAlias("coefficient", "宽放系数");
            writer.addHeaderAlias("CraftCategoryCode","工艺品类编码");
            writer.addHeaderAlias("remark", "备注");
            writer.addHeaderAlias("status", "状态");
            writer.addHeaderAlias("updateUser", "维护人");
            writer.addHeaderAlias("updateTime", "维护时间");
            writer.write(wideCoefficientRepList, true);

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
