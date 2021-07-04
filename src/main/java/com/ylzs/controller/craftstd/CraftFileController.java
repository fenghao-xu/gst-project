package com.ylzs.controller.craftstd;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.CraftFile;
import com.ylzs.service.craftstd.ICraftFileService;
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
 * 说明：工艺文件
 *
 * @author lyq
 * 2019-10-22 13:58
 */
@Api(tags = "工艺文件")
@RestController
@RequestMapping(value = "/craft-file")
public class CraftFileController implements IModuleInfo {
    @Resource
    ICraftFileService craftFileService;
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询所有工艺文件")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftFile>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                          @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<CraftFile> craftFiles = craftFileService.getCraftFileByPage(null, null, null, null);
        PageInfo<CraftFile> pageInfo = new PageInfo<>(craftFiles);
        return Result.ok(craftFiles, pageInfo.getTotal());

    }

    @RequestMapping(value = "/getOne/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询单个工艺文件")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftFile>> getOne(@PathVariable("id") Long id) {
        notNull(id, "工艺文件id不能为空");
        long[] ids = new long[]{id};
        List<CraftFile> craftFiles = craftFileService.getCraftFileById(ids);
        return Result.ok(craftFiles);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加工艺文件")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody CraftFile craftFile) {
        notNull(craftFile.getResourceType(), "资源类型不能为空");
        craftFile.setUpdateUser(currentUser.getUser());
        craftFile.setUpdateTime(new Date());
        Integer ret = craftFileService.addCraftFile(craftFile);
        return Result.ok(ret);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "修改工艺文件")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody CraftFile craftFile) {
        craftFile.setUpdateUser(currentUser.getUser());
        craftFile.setUpdateTime(new Date());
        Integer ret = craftFileService.updateCraftFile(craftFile);
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除工艺文件")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("ids") String ids) {
        notBlank(ids, "工艺文件id不能为空");
        String[] split = ids.split(",");
        Integer ret = 0;
        for (String id: split) {
            int res = craftFileService.deleteCraftFile(Long.parseLong(id));
            if (ret > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE, currentUser.getUser(),
                        "删除工艺文件" + id);
            }
            ret += res;
        }
        return Result.ok(ret);
    }

    @Override
    public String getModuleCode() {
        return "craft-file";
    }

}
