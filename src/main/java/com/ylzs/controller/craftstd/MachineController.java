package com.ylzs.controller.craftstd;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.Machine;
import com.ylzs.service.craftstd.IMachineService;
import com.ylzs.service.system.IOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.*;


/**
 * 说明：机器接口
 *
 * @author lyq
 * 2019-09-30 15:49
 */
@Api(tags = "机器")
@RestController
@RequestMapping(value = "/machine")
public class MachineController implements IModuleInfo {
    @Resource
    IMachineService machineService;
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/getOne/{machineId}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询指定机器信息")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<Machine>> getOne(@PathVariable("machineId") String machineId) {
        notBlank(machineId, "机器代码不能为空");
        Integer[] machineIds = new Integer[]{Integer.parseInt(machineId)};
        List<Machine> machines = machineService.getMachineById(machineIds);
        isTrue(ObjectUtils.isNotEmptyList(machines), "未找到机器");
        return Result.ok(machines);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部机器")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<Machine>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                        @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                        @RequestParam(name = "keywords", required = false) String keywords,
                                        @RequestParam(name = "beginDate", required = false) Date beginDate,
                                        @RequestParam(name = "endDate", required = false) Date endDate,
                                        @RequestParam(name = "workTypeId", required = false) Integer workTypeId,
                                        @RequestParam(name = "deviceId", required = false) Integer deviceId) {
        PageHelper.startPage(page, rows);
        List<Machine> machines = machineService.getMachineByPage(keywords, beginDate, endDate, workTypeId, deviceId);
        PageInfo<Machine> pageInfo = new PageInfo<>(machines);
        return Result.ok(machines, pageInfo.getTotal());
    }

    @RequestMapping(value = "/delete/{machineIds}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除机器")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("machineIds") String machineIds) {
        notBlank(machineIds, "机器代码不能为空");
        String[] split = machineIds.split(",");
        Integer ret = 0;
        for (String machineId : split) {
            int res = machineService.deleteMachine(Integer.parseInt(machineId), currentUser.getUser());
            if (res > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                        currentUser.getUser(), "删除机器" + machineId);
            }
            ret += res;
        }
        return Result.ok(ret);
    }




    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加机器")
    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody Machine machine) {
        notBlank(machine.getMachineCode(), "机器代码不能为空");
        notBlank(machine.getMachineNameCn(), "机器名称(中文)");
        isTrue(machine.getMachineCode().length()==5, "机器代码长度为5位");
        machine.setUpdateUser(currentUser.getUser());
        machine.setUpdateTime(new Date());
        Integer ret = machineService.addMachine(machine);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }



    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新机器")
    @Authentication(auth= Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody Machine machine) {
        notNull(machine.getId(), "机器id不能为空");
        notBlank(machine.getMachineCode(), "机器代码不能为空");
        notBlank(machine.getMachineNameCn(), "机器名称(中文)");
        machine.setUpdateUser(currentUser.getUser());
        machine.setUpdateTime(new Date());
        Integer ret = machineService.updateMachine(machine);
        return Result.ok(ret);
    }

    @RequestMapping(value = "/deleteResource", method = RequestMethod.PUT)
    @ApiOperation(value = "deleteResource", notes = "删除图片资源")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> deleteResource(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                          @RequestParam(name = "machineId", required = false) String machineId,
                                          @RequestParam(name = "fileUrl", required = true) String fileUrl) {
        notBlank(fileUrl, "文件访问路径不能为空");
        Machine machine = null;
        if (machineId != null && !machineId.isEmpty()) {
            Integer[] ids = new Integer[]{Integer.parseInt(machineId)};
            List<Machine> machines = machineService.getMachineById(ids);
            if (machines != null && !machines.isEmpty()) {
                machine = machines.get(0);
            }
            notNull(machine, "未找到机器");
        }
        return Result.ok(machineService.deleteResource(machine, fileUrl, currentUser.getUser()));
    }

    @RequestMapping(value = "/addResource", method = RequestMethod.PUT)
    @ApiOperation(value = "addResource", notes = "添加图片资源")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<String> addResource(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                      @RequestParam(name = "machineId", required = false) String machineId,
                                      @RequestParam(name = "file", required = true) MultipartFile file) {
        notNull(file, "文件不能为空");
        Machine machine = null;
        if (machineId != null && !machineId.isEmpty()) {
            Integer[] ids = new Integer[]{Integer.parseInt(machineId)};
            List<Machine> machines = machineService.getMachineById(ids);
            if (machines != null && !machines.isEmpty()) {
                machine = machines.get(0);
            }
            notNull(machine, "未找到机器");
        }
        return Result.ok(machineService.addResource(machine, file, currentUser.getUser()));
    }


    @Override
    public String getModuleCode() {
        return "machine";
    }


}
