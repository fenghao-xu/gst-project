package com.ylzs.controller.craftstd;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.service.craftstd.ICraftCategoryService;
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
 * @author ：lyq
 * @description：TODO
 * @date ：2020-03-05 16:40
 */
@Api(tags = "工艺品类")
@RestController
@RequestMapping(value = "/craft-category")
public class CraftCategoryController  implements IModuleInfo {
    @Resource
    ICraftCategoryService craftCategoryService;
    @Resource
    IOperationLogService operationLogService;


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询所有工艺品类")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftCategory>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                              @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                              @RequestParam(name = "keywords", required = false) String keywords,
                                              @RequestParam(name = "beginDate", required = false) Date beginDate,
                                              @RequestParam(name = "endDate", required = false) Date endDate) {
        PageHelper.startPage(page, rows);
        List<CraftCategory> craftCategories = craftCategoryService.getCraftCategoryByPage(keywords, beginDate, endDate);
        PageInfo<CraftCategory> pageInfo = new PageInfo<>(craftCategories);
        return Result.ok(craftCategories, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllValidCraftCategory", method = RequestMethod.GET)
    @ApiOperation(value = "getAllValidCraftCategory", notes = "查询所有有效的工艺品类")
    public Result<List<CraftCategory>> getAllValidCraftCategory(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                                @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows){
        PageHelper.startPage(page, rows);
        List<CraftCategory>craftCategories = craftCategoryService.getAllValidCraftCategory();
        PageInfo<CraftCategory> pageInfo = new PageInfo<>(craftCategories);
        return Result.ok(craftCategories, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getOne/{craftCategoryCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询单个工艺品类")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftCategory>> getOne(@PathVariable("craftCategoryCode") String craftCategoryCode) {
        notBlank(craftCategoryCode, "错误的代码");
        String[] craftCategoryCodes = new String[]{craftCategoryCode};
        List<CraftCategory> craftCategorys = craftCategoryService.getCraftCategoryByCode(craftCategoryCodes);
        return Result.ok(craftCategorys);
    }

    @RequestMapping(value = "/getMaxSeqNum", method = RequestMethod.GET)
    @ApiOperation(value = "getMaxSeqNum", notes = "获取最大序号")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<Integer> getMaxSeqNum() {
        Integer seqNum = craftCategoryService.getMaxSeqNum();
        if(seqNum == null || seqNum <= 0) {
            seqNum = 1;
        }
        return Result.ok(seqNum);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加工艺品类")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<String> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                              @RequestBody CraftCategory craftCategory) {
        notBlank(craftCategory.getCraftCategoryCode(), "工艺品类代码不能为空");
        notBlank(craftCategory.getCraftCategoryName(), "工艺品类名称不能为空");
        isTrue(craftCategory.getCraftCategoryCode().length()==4, "工艺品类代码长度不等于4位");

        long randomCode = SnowflakeIdUtil.generateId();
        craftCategory.setRandomCode(randomCode);
        craftCategory.setIsInvalid(null);
        craftCategory.setUpdateTime(new Date());
        craftCategory.setUpdateUser(currentUser.getUser());
        Integer ret = craftCategoryService.addCraftCategory(craftCategory);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }

        return Result.ok(String.valueOf(randomCode));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "修改工艺品类")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody CraftCategory category) {
        notBlank(category.getCraftCategoryCode(), "错误的工艺品类代码");
        category.setIsInvalid(null);
        category.setUpdateTime(new Date());
        category.setUpdateUser(currentUser.getUser());
        Integer ret = craftCategoryService.updateCraftCategory(category);
        if (ret != null && ret > 0) {
            operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.UPDATE,
                    currentUser.getUser(), "更新工艺品类" + category.getCraftCategoryCode());
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{craftCategoryCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除工艺品类")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("craftCategoryCodes") String craftCategoryCodes) {
        notBlank(craftCategoryCodes, "错误的工艺品类代码");
        String[] split = craftCategoryCodes.split(",");

        Integer ret = 0;
        for(String code: split) {
            int res = craftCategoryService.deleteCraftCategory(code, currentUser.getUser());
            if(res > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                        currentUser.getUser(), "删除工艺品类" + code);
            }
            ret += res;
        }

        return Result.ok(ret);
    }

    @Override
    public String getModuleCode() {
        return "craft-category";
    }
}
