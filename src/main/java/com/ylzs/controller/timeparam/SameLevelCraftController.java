package com.ylzs.controller.timeparam;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.system.User;
import com.ylzs.entity.timeparam.SameLevelCraft;
import com.ylzs.service.system.impl.UserService;
import com.ylzs.service.timeparam.impl.SameLevelCraftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: watermelon.xzx
 * @Description:
 * @Date: Created in 15:52 2020/9/15
 */
@Api(tags = "同级工序")
@RestController
@RequestMapping(value = "/same-level-craft")
public class SameLevelCraftController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SameLevelCraftController.class);

    @Resource
    private SameLevelCraftService sameLevelCraftService;
    @Resource
    private UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询所有同级工序")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<SameLevelCraft>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                               @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                               @RequestParam(name = "keywords", required = false) String keywords,
                                               @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                               @RequestParam(name = "makeTypeCode", required = false) String makeTypeCode) {

        PageHelper.startPage(page, rows);
        List<SameLevelCraft> SameLevelCrafts = sameLevelCraftService.getByParam(keywords, craftCategoryCode, makeTypeCode);
        Map<String, User> userMap = getUserMap();
        for(SameLevelCraft sameLevelCraft : SameLevelCrafts){
            if(StringUtils.isNotBlank(sameLevelCraft.getCreateUser())){
                sameLevelCraft.setCreateUserName(userMap.get(sameLevelCraft.getCreateUser()).getUserName());
            }
            if(StringUtils.isNotBlank(sameLevelCraft.getUpdateUser())){
                sameLevelCraft.setUpdateUserName(userMap.get(sameLevelCraft.getUpdateUser()).getUserName());
            }
            if(StringUtils.isNotBlank(sameLevelCraft.getReleaseUser())){
                sameLevelCraft.setReleaseUserName(userMap.get(sameLevelCraft.getUpdateUser()).getUserName());
            }
        }
        PageInfo<SameLevelCraft> pageInfo = new PageInfo<>(SameLevelCrafts);
        return Result.ok(SameLevelCrafts, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getDetails", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询同级工序详情")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<SameLevelCraft> getDetails(@RequestParam(name = "id", required = false) String id) {
        if(StringUtils.isNotBlank(id)){
            Map<String, User> userMap = getUserMap();
            Integer ids = Integer.parseInt(id);
            SameLevelCraft sameLevelCraft = sameLevelCraftService.getDetails(ids);
            if(StringUtils.isNotBlank(sameLevelCraft.getCreateUser())){
                sameLevelCraft.setCreateUserName(userMap.get(sameLevelCraft.getCreateUser()).getUserName());
            }
            if(StringUtils.isNotBlank(sameLevelCraft.getUpdateUser())){
                sameLevelCraft.setUpdateUserName(userMap.get(sameLevelCraft.getUpdateUser()).getUserName());
            }
            if(StringUtils.isNotBlank(sameLevelCraft.getReleaseUser())){
                sameLevelCraft.setReleaseUserName(userMap.get(sameLevelCraft.getUpdateUser()).getUserName());
            }
            return Result.ok(sameLevelCraft);
        }else{
            return Result.fail(MessageConstant.SERVER_FIELD_ERROR);
        }


    }


    /**
     * 新增或者修改
     */
    @RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "addOrUpdate", notes = "新增或者修改")
    public Result<JSONObject> addOrUpdate(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }

        JSONObject result = new JSONObject();
        JSONObject dataObj = JSONObject.parseObject(data);
        //参数校验
        try {
            Result<JSONObject> paramCheckResult = checkParam(dataObj);
            if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
                return paramCheckResult;
            }
        } catch (Exception e) {
            Result.ok(MessageConstant.SERVER_FIELD_ERROR, result.put("msg", e.getMessage()));
        }

        //id
        String id = dataObj.getString("id");
        String operation = dataObj.getString("operateType");
        if (StringUtils.isNotEmpty(id)) {
            LOGGER.info("------同级工序操作类型是update--------");
            operation = BusinessConstants.Send2Pi.actionType_update;
        }
        result = sameLevelCraftService.addOrUpdate(dataObj, operation);

        if (result != null) {
            return Result.ok(MessageConstant.SUCCESS, result);
        } else {
            return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
        }
    }

    /**
     * 物理删除
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除同级工序")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<JSONObject> deleteSameLevelCraft(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                                   @PathVariable("id") String id) throws Exception {
        if(StringUtils.isBlank(id)){
            return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
        }
        long nowId = Long.parseLong(id);
        JSONObject result = sameLevelCraftService.deleteSameLevelCraft(nowId);
        if (result != null) {
            return Result.ok(MessageConstant.SUCCESS, result);
        } else {
            return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
        }

    }

    @ApiOperation(value = "exportSameLevelCraft", notes = "导出同级工序数据清单", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportSameLevelCraftList", method = RequestMethod.GET)
    public Result exportSameLevelCraft(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                            @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                            @RequestParam(name = "keywords", required = false) String keywords,
                                            @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                            @RequestParam(name = "makeTypeCode", required = false) String makeTypeCode,
                                            HttpServletResponse response) throws Exception {
        List<SameLevelCraft> SameLevelCrafts = sameLevelCraftService.getByParam(keywords, craftCategoryCode, makeTypeCode);
        if (CollUtil.isEmpty(SameLevelCrafts)) {
            return Result.ok("无数据导出");
        }
        List<SameLevelCraft> wideCoefficientRepList = new ArrayList<>(SameLevelCrafts.size());
        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名
            writer.addHeaderAlias("sameLevelCraftNumericalCode", "同级工序编码");
            writer.addHeaderAlias("sameLevelCraftName", "同级工序名称");
            writer.addHeaderAlias("sameLevelCraftSerial", "同级工序流水号");
            writer.addHeaderAlias("craftCategoryName","工艺品类");
            writer.addHeaderAlias("makeTypeName", "做工类型");
            writer.addHeaderAlias("makeTypeNumericalCode", "做工类型代码");
            writer.addHeaderAlias("hardLevel", "难度等级");
            writer.addHeaderAlias("remark", "备注");
            writer.addHeaderAlias("status", "状态");
            writer.addHeaderAlias("createUserName", "创建人");
            writer.addHeaderAlias("createTime", "创建时间");
            writer.addHeaderAlias("updateUserName", "更新人");
            writer.addHeaderAlias("updateTime", "更新时间");
            writer.addHeaderAlias("releaseUserName", "发布人");
            writer.addHeaderAlias("releaseTime", "发布时间");
            writer.write(wideCoefficientRepList, true);
            String fileName = URLEncoder.encode("同级工序数据清单.xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            LOGGER.error("exportMaterialCraftPageData fails", e);
        } finally {
            writer.close();
            if (null != out) {
                IoUtil.close(out);
            }
        }
        return null;
    }






    /**
     * 校验参数
     * operationType 操作类型，因为add和update校验有一点不同，update要必须要验证randomCode和Id不为空
     */
    public Result<JSONObject> checkParam(JSONObject dataObj) throws Exception {
        //登录用户
        String userCode = dataObj.getString("userCode");
        if (StringUtils.isEmpty(userCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "登录用户为空");
        }
        //登录用户
        String status = dataObj.getString("status");
        if (StringUtils.isEmpty(status)) {
            return Result.build(MessageConstant.PARAM_NULL, "状态为空");
        }
        String sameLevelCraftName = dataObj.getString("sameLevelCraftName");
        if (StringUtils.isEmpty(sameLevelCraftName)) {
            return Result.build(MessageConstant.PARAM_NULL, "同级工序名称为空");
        }
        String craftCategoryCode = dataObj.getString("craftCategoryCode");
        if (StringUtils.isEmpty(craftCategoryCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "工艺品类为空");
        }
        String makeTypeCode = dataObj.getString("makeTypeCode");
        if (StringUtils.isEmpty(makeTypeCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "做工为空");
        }
        return Result.build(MessageConstant.SUCCESS, "参数校验OK");
    }

    public Map<String,User> getUserMap(){
        List<User> users = userService.getAllUser();
        Map<String,User> userMap = new HashMap<>();
        for(User user : users){
            userMap.put(user.getUserCode(),user);
        }
        return userMap;
    }





}
