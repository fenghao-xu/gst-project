package com.ylzs.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.system.OperationLog;
import com.ylzs.service.system.IOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.notBlank;
import static com.ylzs.common.util.Assert.notNull;

/**
 * 说明：操作日志
 *
 * @author Administrator
 * 2019-10-21 16:52
 */
@Api(tags = "操作日志")
@RestController
@RequestMapping(value = "/oper-log")
public class OperationLogController implements IModuleInfo {
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

//    @PostMapping(value = "/add")
//    @ApiOperation(value = "add", notes = "添加操作日志")
//    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
//    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
//                               @RequestParam(name = "用户代码", required = true) String userCode,
//                               @RequestParam(name = "模块代码", required = true) String moduleCode,
//                               @RequestParam(name = "操作代码", required = true) String operCode,
//                               @RequestParam(name = "操作描述", required = true) String operDesc
//                               ) {
//        notBlank(userCode, "用户代码不能为空");
//        notBlank(moduleCode, "模块代码不能为空");
//        OperationLog operationLog = new OperationLog();
//        operationLog.setUserCode(userCode);
//        operationLog.setModuleCode(moduleCode);
//        operationLog.setOperCode(operCode);
//        operationLog.setOperDesc(operDesc);
//        operationLog.setCreateTime(new Date());
//        Integer ret = operationLogService.add(operationLog);
//        return Result.ok(ret);
//    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加操作日志")
    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody OperationLog operationLog
    ) {
        notBlank(operationLog.getUserCode(), "用户代码不能为空");
        notBlank(operationLog.getModuleCode(), "模块代码不能为空");
        operationLog.setCreateTime(new Date());
        Integer ret = operationLogService.addOperationLog(operationLog);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除操作日志")
    @Authentication(auth= Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("ids") String ids) {
        notBlank(ids, "操作日志id不能为空");
        String[] split = ids.split(",");
        Integer ret = 0;
        for (String id: split) {
            int res = operationLogService.deleteOperationLog(Long.parseLong(id));
            ret += res;
        }
        return Result.ok(ret);
    }

//    @RequestMapping(value = "/update", method = RequestMethod.PUT)
//    @ApiOperation(value = "update", notes = "更新操作日志")
//    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
//    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
//                                  @RequestParam(name = "操作日志id", required = true) Long id,
//                                  @RequestParam(name = "用户代码", required = true) String userCode,
//                                  @RequestParam(name = "模块代码", required = true) String moduleCode,
//                                  @RequestParam(name = "操作代码", required = true) String operCode,
//                                  @RequestParam(name = "操作描述", required = true) String operDesc) {
//        notNull(id, "操作日志id不能为空");
//        OperationLog operationLog = operationLogService.getOne(id);
//        notNull(id, "非法id");
//        operationLog.setUserCode(userCode);
//        operationLog.setModuleCode(moduleCode);
//        operationLog.setOperCode(operCode);
//        operationLog.setOperDesc(operDesc);
//        operationLog.setCreateTime(new Date());
//        Integer ret = operationLogService.update(operationLog);
//        return Result.ok(ret);
//    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新操作日志")
    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody OperationLog operationLog) {
        notNull(operationLog.getId(), "操作日志id不能为空");
        operationLog.setCreateTime(new Date());
        Integer ret = operationLogService.updateOperationLog(operationLog);
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getOne/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询操作日志")
    @Authentication(auth= Authentication.AuthType.QUERY, required = true)
    public Result<List<OperationLog>> getOne(@PathVariable("id") Long id) {
        notNull(id, "操作日志id不能为空");
        long[] ids = new long[]{id};
        List<OperationLog> operationLogs = operationLogService.getOperationLogById(ids);
        return Result.ok(operationLogs);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部操作日志")
    @Authentication(auth= Authentication.AuthType.QUERY, required = true)
    public Result<List<OperationLog>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                             @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                             @RequestParam(name = "keywords", required = false) String keywords,
                                             @RequestParam(name = "beginDate", required = false) Date beginDate,
                                             @RequestParam(name = "endDate", required = false) Date endDate,
                                             @RequestParam(name = "moduleCodes", required = false) String moduleCodes,
                                             @RequestParam(name = "operCodes", required = false) String operCodes) {
        String[] moduleCodeList = null;
        String[] operCodeList = null;
        if(moduleCodes != null && !moduleCodes.isEmpty()) {
            moduleCodeList = moduleCodes.split(",");
        }
        if(operCodes != null && !operCodes.isEmpty()) {
            operCodeList = operCodes.split(",");
        }

        PageHelper.startPage(page, rows);
        List<OperationLog> operationLogs = operationLogService.getOperationLogByPage(keywords, beginDate, endDate,
                moduleCodeList, operCodeList);
        PageInfo<OperationLog> pageInfo = new PageInfo<>(operationLogs);
        return Result.ok(operationLogs, pageInfo.getTotal());
    }


    @RequestMapping(value = "/getSysInOutLog", method = RequestMethod.GET)
    @ApiOperation(value = "getSysInOutLog", notes = "查询全部登入登出日志")
    @Authentication(auth= Authentication.AuthType.QUERY, required = true)
    public Result<List<OperationLog>> getSysInOutLog(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                             @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                             @RequestParam(name = "keywords", required = false) String keywords,
                                             @RequestParam(name = "beginDate", required = false) Date beginDate,
                                             @RequestParam(name = "endDate", required = false) Date endDate) {
        String[] moduleCodes = {"auth"};
        String[] operCodes = {"I","O"};
        PageHelper.startPage(page, rows);
        List<OperationLog> operationLogs = operationLogService.getOperationLogByPage(keywords, beginDate, endDate,
                moduleCodes, operCodes);
        PageInfo<OperationLog> pageInfo = new PageInfo<>(operationLogs);
        return Result.ok(operationLogs, pageInfo.getTotal());
    }


    @Override
    public String getModuleCode() {
        return "oper-log";
    }



}
