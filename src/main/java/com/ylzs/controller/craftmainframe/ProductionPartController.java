package com.ylzs.controller.craftmainframe;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftmainframe.ProductionPart;
import com.ylzs.service.craftmainframe.IProductionPartService;
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

/**
 * @author ：lyq
 * @description：TODO
 * @date ：2020-03-05 16:37
 */
@Api(tags = "生产部件")
@RestController
@RequestMapping(value = "/production-part")
public class ProductionPartController implements IModuleInfo {
    @Resource
    IProductionPartService productionPartService;

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
    @ApiOperation(value = "getAll", notes = "查询所有生产部件")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<ProductionPart>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                                                       @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                                                       @RequestParam(name = "craftCategoryRandomCode", required = false) Long craftCategoryRandomCode,
                                                                                       @RequestParam(name = "craftMainFrameRandomCode", required = false) Long craftMainFrameRandomCode,
                                                                                       @RequestParam(name = "keywords", required = false) String keywords,
                                                                                       @RequestParam(name = "beginDate", required = false) Date beginDate,
                                                                                       @RequestParam(name = "endDate", required = false) Date endDate) {
        PageHelper.startPage(page, rows);
        List<ProductionPart> productionParts = productionPartService.getByCondition(
                craftCategoryRandomCode,craftMainFrameRandomCode,keywords,beginDate,endDate
        );
        PageInfo<ProductionPart> pageInfo = new PageInfo<>(productionParts);
        return Result.ok(productionParts, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getOne/{randomCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询单个生产部件")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<ProductionPart> getOne(@PathVariable("randomCode") String randomCode) {
        notBlank(randomCode, "错误的生产部件关联代码");
        return Result.ok(productionPartService.selectByPrimaryKey(Long.parseLong(randomCode)));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加生产部件")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<String> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                              @RequestBody ProductionPart productionPart) {
        notBlank(productionPart.getMainFrameCode(), "工艺主框架代码不能为空");
        notBlank(productionPart.getMainFrameName(), "工艺主框架名称不能为空");
        notNull(productionPart.getMainFrameRandomCode(), "工艺主框架关联代码不能为空");
        notBlank(productionPart.getCraftCategoryCode(), "工艺品类代码不能为空");
        notBlank(productionPart.getCraftCategoryName(), "工艺品类名称不能为空");
        notNull(productionPart.getCraftCategoryRandomCode(), "工艺品类关联代码不能为空");
        notBlank(productionPart.getProductionPartName(), "生产部件不能为空");
        long randomCode = SnowflakeIdUtil.generateId();
        productionPart.setRandomCode(randomCode);

        String preStr = productionPart.getCraftCategoryCode().substring(0,1)+"Z";
        String code = maxCodeService.getNextSerialNo(getModuleCode(), preStr, 2, false);

        productionPart.setProductionPartCode(code);
        productionPart.setCreateTime(new Date());
        productionPart.setCreateUser(currentUser.getUserCode());
        productionPart.setUpdateTime(productionPart.getCreateTime());
        productionPart.setUpdateUser(currentUser.getUserCode());
        productionPart.setStatus(Const.COMMON_STATUS_SAVE);
        productionPart.setIsInvalid(false);
        int ret = productionPartService.insert(productionPart);
        if(ret != 1) {
            return Result.build(-1, "保存失败");
        }
        return  Result.ok(String.valueOf(randomCode));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "修改生产部件")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody ProductionPart productionPart) {
        notNull(productionPart.getRandomCode(), "工艺主框架关联代码不能为空");

        productionPart.setId(null);
        productionPart.setUpdateUser(currentUser.getUserCode());
        productionPart.setUpdateTime(new Date());
        return Result.ok(productionPartService.updateByPrimaryKeySelective(productionPart));
    }

    @RequestMapping(value = "/delete/{randomCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除生产部件")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("randomCodes") String randomCodes) {
        notBlank(randomCodes, "生产部件关联代码不能为空");
        String[] split = randomCodes.split(",");
        int ret = 0;
        for(String code: split) {
            ret += productionPartService.deleteByPrimaryKey(Long.parseLong(code));
        }
        return Result.ok(ret);
    }


    @Override
    public String getModuleCode() {
        return "production-part";
    }
}
