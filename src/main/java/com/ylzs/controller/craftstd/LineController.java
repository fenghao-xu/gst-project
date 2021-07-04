package com.ylzs.controller.craftstd;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.Line;
import com.ylzs.service.craftstd.ILineService;
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

import static com.ylzs.common.util.Assert.isTrue;
import static com.ylzs.common.util.Assert.notBlank;

/**
 * 说明：线号接口
 *
 * @author lyq
 * 2019-09-30 16:54
 */
@Api(tags = "线号")
@RestController
@RequestMapping(value = "/line")
public class LineController implements IModuleInfo {
    @Resource
    ILineService lineService;
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加线号")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody Line line) {
        notBlank(line.getLineCode(), "线号代码不能为空");
        notBlank(line.getLineName(), "线号名称不能为空");
        isTrue(line.getLineCode().length() == 4, "线号代码长度必须为4位");

        line.setUpdateUser(currentUser.getUser());
        line.setUpdateTime(new Date());
        Integer ret = lineService.addLine(line);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{lineCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除线号")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("lineCodes") String lineCodes) {
        notBlank(lineCodes, "线号代码不能为空");
        String[] split = lineCodes.split(",");
        List<Line> lines = lineService.getLineByCode(split);

        Integer ret = 0;
        int lineRet;
        if (lines != null && lines.size() == split.length) {
            for(Line line: lines) {
                Integer count = lineService.getStitchCountByCode(line.getLineCode());
                if(count != null && count > 0) {
                    line.setIsInvalid(true);
                    line.setUpdateTime(new Date());
                    line.setUpdateUser(currentUser.getUser());
                    lineRet = lineService.updateLine(line);
                } else {
                    lineRet = lineService.deleteLine(line.getLineCode());
                }

                if(lineRet > 0) {
                    ret += lineRet;
                    operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                            currentUser.getUser(), "删除线号" + line.getLineCode());
                }
            }

        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新线号")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody Line line) {
        notBlank(line.getLineCode(), "线号代码不能为空");
        notBlank(line.getLineName(), "线号名称不能为空");
        line.setUpdateUser(currentUser.getUser());
        line.setUpdateTime(new Date());
        Integer ret = lineService.updateLine(line);
        return Result.ok(ret);
    }


    @RequestMapping(value = "/getOne/{lineCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询线号")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<Line>> getOne(@PathVariable("lineCode") String lineCode) {
        notBlank(lineCode, "线号代码不能为空");
        String[] lineCodes = new String[]{lineCode};
        List<Line> lines = lineService.getLineByCode(lineCodes);
        return Result.ok(lines);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部线号")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<Line>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                     @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                     @RequestParam(name = "keywords", required = false) String keywords,
                                     @RequestParam(name = "beginDate", required = false) Date beginDate,
                                     @RequestParam(name = "endDate", required = false) Date endDate) {
        PageHelper.startPage(page, rows);
        List<Line> lines = lineService.getLineByPage(keywords, beginDate, endDate);
        PageInfo<Line> pageInfo = new PageInfo<>(lines);
        return Result.ok(lines, pageInfo.getTotal());
    }

    @Override
    public String getModuleCode() {
        return "line";
    }
}
