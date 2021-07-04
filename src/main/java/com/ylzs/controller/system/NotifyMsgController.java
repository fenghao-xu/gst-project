package com.ylzs.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.system.NotifyMsg;
import com.ylzs.service.system.INotifyMsgService;
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

@Api(tags = "通知消息")
@RestController
@RequestMapping(value = "/notify-msg")
public class NotifyMsgController implements IModuleInfo {
    @Resource
    INotifyMsgService notifyMsgService;


    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加通知消息")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody NotifyMsg notifyMsg) {
        notBlank(notifyMsg.getMsgTxt(), "通知消息不能为空");
        notNull(notifyMsg.getMsgType(), "消息类型不能为空");
        isTrue(notifyMsg.getMsgKeyId() != null || notifyMsg.getMsgKeyCode() != null, "消息关键id或关键code不能为空");

        notifyMsg.setCreateTime(new Date());
        notifyMsg.setIsRead(false);
        notifyMsg.setReadTime(null);

        Integer ret = notifyMsgService.addNotifyMsg(notifyMsg);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除通知消息")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("ids") String ids) {
        notBlank(ids, "id不能为空");
        String[] split = ids.split(",");

        Integer ret = 0;

        if (split != null && split.length > 0) {
            for (String id : split) {
                ret += notifyMsgService.deleteNotifyMsg(Long.parseLong(id));
            }
        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新通知消息")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody NotifyMsg notifyMsg) {
        notNull(notifyMsg.getId(), "id不能为空");
        Integer ret = notifyMsgService.updateNotifyMsg(notifyMsg.getId(), currentUser.getUserCode());
        return Result.ok(ret);
    }


    @RequestMapping(value = "/getOne/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询通知消息")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<NotifyMsg>> getOne(@PathVariable("id") Long id) {
        notNull(id, "id不能为空");

        List<NotifyMsg> notifyMsgList = notifyMsgService.getNotifyMsgById(id);
        return Result.ok(notifyMsgList);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部通知消息")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<NotifyMsg>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                          @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                          @RequestParam(name = "keywords", required = false) String keywords,
                                          @RequestParam(name = "beginDate", required = false) Date beginDate,
                                          @RequestParam(name = "endDate", required = false) Date endDate,
                                          @RequestParam(name = "isRead", required = false) Boolean isRead,
                                          @RequestParam(name = "readUser", required = true) String readUser
    ) {
        PageHelper.startPage(page, rows);
        List<NotifyMsg> notifyMsgList = notifyMsgService.getNotifyMsgByPage(isRead, keywords, beginDate, endDate,readUser);
        PageInfo<NotifyMsg> pageInfo = new PageInfo<>(notifyMsgList);
        return Result.ok(notifyMsgList, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getNotifyMsgType", method = RequestMethod.GET)
    @ApiOperation(value = "getNotifyMsgType", notes = "查询通知消息类型")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<INotifyMsgService.NotifyMsgType[]> getNotifyMsgType() {
        return Result.ok(notifyMsgService.getNotifyMsgType());
    }

    @Override
    public String getModuleCode() {
        return "notify-msg";
    }

}
