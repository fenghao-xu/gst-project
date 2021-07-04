package com.ylzs.controller.craftstd;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.MakeType;
import com.ylzs.service.craftstd.IMakeTypeService;
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

import static com.ylzs.common.util.Assert.*;

/**
 * 说明：做工类型接口
 *
 * @author lyq
 * 2019-09-30 16:51
 */
@Api(tags = "做工类型")
@RestController
@RequestMapping(value = "/make-type")
public class MakeTypeController implements IModuleInfo {
    @Resource
    IMakeTypeService makeTypeService;
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加做工类型")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody MakeType makeType) {
        notBlank(makeType.getMakeTypeCode(), "做工类型代码不能为空");
        notBlank(makeType.getMakeTypeName(), "做工类型名称不能为空");
        notNull(makeType.getWorkTypeId(), "工种类型代码不能为空");
        isTrue(makeType.getMakeTypeCode().length()==2, "做工类型代码长度只能为2位");
        makeType.setUpdateUser(currentUser.getUser());
        makeType.setUpdateTime(new Date());
        Integer ret = makeTypeService.addMakeType(makeType);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/delete/{makeTypeCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除做工类型")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("makeTypeCodes") String makeTypeCodes) {
        notBlank(makeTypeCodes, "做工类型代码不能为空");
        String[] split = makeTypeCodes.split(",");
        Integer ret = 0;
        for (String makeTypeCode : split) {
            int res = makeTypeService.deleteMakeType(makeTypeCode, currentUser.getUser());
            if (res > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                        currentUser.getUser(), "删除做工类型" + makeTypeCode);
            }
            ret += res;
        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新做工类型")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody MakeType makeType) {
        notBlank(makeType.getMakeTypeCode(), "做工类型代码不能为空");
        notBlank(makeType.getMakeTypeName(), "做工类型名称不能为空");
        notNull(makeType.getWorkTypeId(), "工种类型代码不能为空");

        makeType.setUpdateUser(currentUser.getUser());
        makeType.setUpdateTime(new Date());
        Integer ret = makeTypeService.updateMakeType(makeType);
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getOne/{makeTypeCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询做工类型")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<MakeType>> getMakeTypeByCode(@PathVariable("makeTypeCode") String makeTypeCode) {
        notBlank(makeTypeCode, "做工类型代码不能为空");
        String[] makeTypeCodes = new String[]{makeTypeCode};
        List<MakeType> makeTypes = makeTypeService.getMakeTypeByCode(makeTypeCodes);
        return Result.ok(makeTypes);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部做工类型")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<MakeType>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                         @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                         @RequestParam(name = "keywords", required = false) String keywords,
                                         @RequestParam(name = "beginDate", required = false) Date beginDate,
                                         @RequestParam(name = "endDate", required = false) Date endDate,
                                         @RequestParam(name = "workTypeId", required = false) Integer workTypeId) {
        PageHelper.startPage(page, rows);
        List<MakeType> makeTypes = makeTypeService.getMakeTypeByPage(keywords, beginDate, endDate,workTypeId);
        PageInfo<MakeType> pageInfo = new PageInfo<>(makeTypes);
        return Result.ok(makeTypes, pageInfo.getTotal());
    }

    @Override
    public String getModuleCode() {
        return "make-type";
    }
}
