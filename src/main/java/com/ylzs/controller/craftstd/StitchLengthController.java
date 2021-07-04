package com.ylzs.controller.craftstd;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.StitchLength;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.craftstd.IStitchLengthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.*;

/**
 * 说明：针距管理接口
 *
 * @author lyq
 * 2019-09-30 16:55
 */
@Api(tags = "针距")
@RestController
@RequestMapping(value = "/stitch-length")
public class StitchLengthController implements IModuleInfo {
    @Resource
    IStitchLengthService stitchLengthService;
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "针距")
    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody StitchLength stitchLength) {
        notBlank(stitchLength.getStitchLengthCode(), "错误的代码");
        notBlank(stitchLength.getStitchLengthName(), "错误的名称");
        notNull(stitchLength.getLineId(), "错误的线型id");
        isTrue(stitchLength.getStitchLengthCode().length()==6, "针距代码长度必须为6位");

        stitchLength.setUpdateUser(currentUser.getUser());
        stitchLength.setUpdateTime(new Date());
        Integer ret = stitchLengthService.addStitchLength(stitchLength);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{stitchLengthCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除针距")
    @Authentication(auth= Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("stitchLengthCodes") String stitchLengthCodes) {
        notBlank(stitchLengthCodes, "错误的代码");
        String[] split = stitchLengthCodes.split(",");
        Integer ret = 0;
        for (String stitchLengthCode: split) {
            int res = stitchLengthService.deleteStitchLength(stitchLengthCode, currentUser.getUser());
            if(res > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                        currentUser.getUser(), "删除针距"+stitchLengthCode);
            }
            ret += res;
        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新针距")
    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody StitchLength stitchLength) {
        notBlank(stitchLength.getStitchLengthCode(), "错误的代码");
        stitchLength.setUpdateUser(currentUser.getUser());
        stitchLength.setUpdateTime(new Date());
        Integer ret = stitchLengthService.updateStitchLength(stitchLength);
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getOne/{stitchLengthCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询针距")
    @Authentication(auth= Authentication.AuthType.QUERY, required = true)
    public Result<List<StitchLength>> getOne(@PathVariable("stitchLengthCode") String stitchLengthCode) {
        String[] stitchLengthCodes = new String[]{stitchLengthCode};
        List<StitchLength> stitchLengths = stitchLengthService.getStitchLengthByCode(stitchLengthCodes);
        return Result.ok(stitchLengths);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部针距")
    @Authentication(auth= Authentication.AuthType.QUERY, required = true)
    public Result<List<StitchLength>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                             @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                             @RequestParam(name = "keywords", required = false) String keywords,
                                             @RequestParam(name = "beginDate", required = false) Date beginDate,
                                             @RequestParam(name = "endDate", required = false) Date endDate,
                                             @RequestParam(name = "lineId", required = false) Integer lineId) {
        PageHelper.startPage(page, rows);
        List<StitchLength> stitchLengths = stitchLengthService.getStitchLengthByPage(keywords, beginDate, endDate, lineId);
        PageInfo<StitchLength> pageInfo = new PageInfo<>(stitchLengths);
        return Result.ok(stitchLengths, pageInfo.getTotal());
    }

    @Override
    public String getModuleCode() {
        return "stitch-length";
    }
}
