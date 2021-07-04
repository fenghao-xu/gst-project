package com.ylzs.controller.craftmainframe;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.service.craftmainframe.ICraftMainFrameRouteService;
import com.ylzs.service.craftstd.IMaxCodeService;
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

@Api(tags = "工艺主框架路线图")
@RestController
@RequestMapping(value = "/craft-main-frame-route")
public class CraftMainFrameRouteController implements IModuleInfo {
    @Resource
    ICraftMainFrameRouteService craftMainFrameRouteService;

    @Resource
    IMaxCodeService maxCodeService;

    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询所有工艺主框架路线图")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftMainFrameRoute>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                    @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                    @RequestParam(name = "mainFrameRandomCode", required = false) Long mainFrameRandomCode,
                                                    @RequestParam(name = "keywords", required = false) String keywords,
                                                    @RequestParam(name = "beginDate", required = false) Date beginDate,
                                                    @RequestParam(name = "endDate", required = false) Date endDate) {
        PageHelper.startPage(page, rows);
        List<CraftMainFrameRoute> craftMainFrameRoutes = craftMainFrameRouteService.getByCondition(mainFrameRandomCode, keywords, beginDate, endDate);
        PageInfo<CraftMainFrameRoute> pageInfo = new PageInfo<>(craftMainFrameRoutes);
        return Result.ok(craftMainFrameRoutes, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getOne/{randomCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询单个工艺主框架路线图")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<CraftMainFrameRoute> getOne(@PathVariable("randomCode") String randomCode) {
        notBlank(randomCode, "错误的工艺主框架路线图关联代码");
        return Result.ok(craftMainFrameRouteService.selectByPrimaryKey(Long.parseLong(randomCode)));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加工艺主框架路线图")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<String> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody CraftMainFrameRoute craftMainFrameRoute) {
        notNull(craftMainFrameRoute.getMainFrameRandomCode(), "工艺主框架关联不能为空");
        notNull(craftMainFrameRoute.getProductionPartRandomCode(), "当前生产部件关联代码不能为空");
        notBlank(craftMainFrameRoute.getProductionPartCode(), "当前生产部件编码不能为空");
        notBlank(craftMainFrameRoute.getProductionPartName(), "当前生产部件名称不能为空");
        notNull(craftMainFrameRoute.getNextProductionPartRandomCode(), "后续生产部件关联代码不能为空");
        notBlank(craftMainFrameRoute.getNextProductionPartCode(), "后续生产部件编码不能为空");
        notBlank(craftMainFrameRoute.getNextProductionPartName(), "后续生产部件名称不能为空");

        long randomCode = SnowflakeIdUtil.generateId();
        craftMainFrameRoute.setRandomCode(randomCode);
        craftMainFrameRoute.setCreateTime(new Date());
        craftMainFrameRoute.setCreateUser(currentUser.getUserCode());
        craftMainFrameRoute.setUpdateTime(craftMainFrameRoute.getCreateTime());
        craftMainFrameRoute.setUpdateUser(currentUser.getUserCode());
        craftMainFrameRoute.setStatus(Const.COMMON_STATUS_SAVE);
        craftMainFrameRoute.setIsInvalid(false);
        int ret = craftMainFrameRouteService.insert(craftMainFrameRoute);
        if(ret != 1) {
            return Result.build(-1, "保存失败");
        }
        return  Result.ok(String.valueOf(randomCode));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "修改工艺主框架路线图")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody CraftMainFrameRoute craftMainFrameRoute) {
        notNull(craftMainFrameRoute.getRandomCode(), "工艺主框架路线图关联代码不能为空");

        craftMainFrameRoute.setId(null);
        craftMainFrameRoute.setUpdateUser(currentUser.getUserCode());
        craftMainFrameRoute.setUpdateTime(new Date());
        return Result.ok(craftMainFrameRouteService.updateByPrimaryKeySelective(craftMainFrameRoute));
    }

    @RequestMapping(value = "/delete/{randomCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除工艺主框架路线图")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("randomCodes") String randomCodes) {
        notBlank(randomCodes, "工艺主框架路线图关联代码不能为空");
        String[] split = randomCodes.split(",");

        int ret = 0;
        for(String code: split) {
            ret += craftMainFrameRouteService.deleteByPrimaryKey(Long.parseLong(code));
        }
        return Result.ok(ret);
    }



    @Override
    public String getModuleCode() {
        return "craft-main-frame-route";
    }

}
