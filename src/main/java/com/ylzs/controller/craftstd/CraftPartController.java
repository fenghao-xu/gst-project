package com.ylzs.controller.craftstd;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.entity.craftstd.CraftPart;
import com.ylzs.entity.system.WebConfig;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.craftstd.ICraftPartService;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.system.IWebConfigService;
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
 * 说明：工艺部件的接口类
 *
 * @author lyq
 * 2019-09-30 16:53
 */
@Api(tags = "工艺部件")
@RestController
@RequestMapping(value = "/craft-part")
public class CraftPartController implements IModuleInfo {
    @Resource
    ICraftPartService craftPartService;
    @Resource
    IOperationLogService operationLogService;
    @Resource
    ICraftCategoryService craftCategoryService;
    @Resource
    IWebConfigService webConfigService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加工艺部件")
    @Authentication(auth = Authentication.AuthType.INSERT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody CraftPart craftPart) {
        notBlank(craftPart.getCraftPartCode(), "工艺部件代码不能为空");
        notBlank(craftPart.getCraftPartName(), "工艺部件名称不能为空");
        isTrue(craftPart.getCraftPartCode().length()<=3, "工艺部件代码长度不能超过3位");
        notNull(craftPart.getCraftCategoryId(), "工艺品类id不能为空");
        CraftCategory craftCategory = craftCategoryService.getCraftCategoryById(craftPart.getCraftCategoryId());
        if(null != craftCategory) {
            craftPart.setCraftCategoryCode(craftCategory.getCraftCategoryCode());
            craftPart.setCraftCategoryName(craftCategory.getCraftCategoryName());
        }


        craftPart.setUpdateUser(currentUser.getUser());
        craftPart.setUpdateTime(new Date());
        Integer ret = craftPartService.addCraftPart(craftPart);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{craftPartCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除工艺部件")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                 @PathVariable("craftPartCodes") String craftPartCodes) {
        notBlank(craftPartCodes, "工艺部件不能为空");
        String[] split = craftPartCodes.split(",");
        Integer ret = 0;
        for (String craftPartCode: split) {
            int res = craftPartService.deleteCraftPart(craftPartCode, currentUser.getUser());
            if (res > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                        currentUser.getUser(), "删除工艺部件" + craftPartCode);
            }
            ret += res;
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新工艺部件")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody CraftPart craftPart) {
        notBlank(craftPart.getCraftPartCode(), "工艺品类代码不能为空");
        craftPart.setUpdateUser(currentUser.getUser());
        craftPart.setUpdateTime(new Date());
        Integer ret = craftPartService.updateCraftPart(craftPart);
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getOne/{craftPartCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询工艺部件")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftPart>> getOne(@PathVariable("craftPartCode") String craftPartCode) {
        notBlank(craftPartCode, "工艺部件代码不能为空");
        String[] craftPartCodes = new String[]{craftPartCode};
        List<CraftPart> craftParts = craftPartService.getCraftPartByCode(craftPartCodes);
        return Result.ok(craftParts);
    }






    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部工艺部件")
//    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftPart>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                          @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                          @RequestParam(name = "keywords", required = false) String keywords,
                                          @RequestParam(name = "craftCategoryId", required = false) Integer craftCategoryId,
                                          @RequestParam(name = "beginDate", required = false) Date beginDate,
                                          @RequestParam(name = "endDate", required = false) Date endDate,
                                          @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode) {
        PageHelper.startPage(page, rows);
        List<CraftPart> craftParts = craftPartService.getCraftPartByPage(keywords, craftCategoryId, beginDate, endDate,craftCategoryCode);
        PageInfo<CraftPart> pageInfo = new PageInfo<>(craftParts);
        return Result.ok(craftParts, pageInfo.getTotal());
    }


    @RequestMapping(value = "/getCraftCategoryPart", method = RequestMethod.GET)
    @ApiOperation(value = "getCraftCategoryPart", notes = "查询款式部件")
//    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftCategory>> getCraftCategoryPart() {
        List<WebConfig> webConfigList = webConfigService.getConfigList();
        List<CraftCategory> craftCategories = craftCategoryService.getCraftCategoryAndPart();
        return Result.ok(craftCategories);
    }

    @RequestMapping(value = "/getCraftCategoryCodeAndName", method = RequestMethod.GET)
    @ApiOperation(value = "getCraftCategoryCodeAndName", notes = "查询款式编码和名称")
//    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftCategory>> getCraftCategoryCodeAndName() {
        List<CraftCategory> craftCategories = craftCategoryService.getCraftCategoryCodeAndName();
        return Result.ok(craftCategories);
    }

    @Override
    public String getModuleCode() {
        return "craft-part";
    }
}
