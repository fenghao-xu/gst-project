package com.ylzs.controller.datadictionary;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.AuthenticationInterceptor;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.datadictionary.DictionaryType;
import com.ylzs.entity.system.User;
import com.ylzs.service.datadictionary.IDictionaryTypeService;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.system.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ylzs.common.util.Assert.notBlank;
import static com.ylzs.common.util.Assert.notNull;

/**
 * @Author: watermelon.xzx
 * @Description:字典值类型
 * @Date: Created in 13:19 2020/2/28
 */
@Api(tags = "字典值类型")
@RestController
@RequestMapping(value = "/dictype")
public class DictionaryTypeController implements IModuleInfo {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Resource
    private IDictionaryTypeService iDictionaryTypeService;
    @Resource
    private IUserService iUserService;
    @Resource
    IOperationLogService operationLogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部字典值类型")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<DictionaryType>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                               @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                               @RequestParam(name = "keywords", required = false) String keywords,
                                               @RequestParam(name = "beginDate", required = false) Date beginDate,
                                               @RequestParam(name = "endDate", required = false) Date endDate) {

        PageHelper.startPage(page, rows);
        List<User> userList = iUserService.getUserByPage(null, null, null, null);
        Map<String,String> userMap = new HashMap<>();
        for(User user:userList){
            userMap.put(user.getUserCode(),user.getUserName());
        }
        List<DictionaryType> dictionaryTypes = iDictionaryTypeService.getDictionaryTypeByPage(keywords,beginDate,endDate);
        List<DictionaryType> newdictionaryTypes = new ArrayList<>();
        for(DictionaryType dictionaryType: dictionaryTypes){

            if(dictionaryType.getCreateUser()!=null){
                String userName = userMap.get(dictionaryType.getCreateUser());
                dictionaryType.setCreateUser(userName);

            }
            if(dictionaryType.getUpdateUser()!=null){
                String userName = userMap.get(dictionaryType.getUpdateUser());
                dictionaryType.setUpdateUser(userName);
            }
            newdictionaryTypes.add(dictionaryType);
        }

        PageInfo<DictionaryType> pageInfo = new PageInfo<>(newdictionaryTypes);
        return Result.ok(newdictionaryTypes, pageInfo.getTotal());
    }



    @RequestMapping(value = "/getOne/{dictionaryTypeCodes}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询指定字典")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<Object> getOne(@PathVariable("dictionaryTypeCodes") String dictionaryTypeCodes) {
        notNull(dictionaryTypeCodes, "dictionaryTypeCodes不能为空");
        DictionaryType dictionaryType = iDictionaryTypeService.getOneBydictionaryTypeCode(dictionaryTypeCodes);
        return Result.ok(dictionaryType);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加字典值类型")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody String jsonString) { //DictionaryType dictionaryType
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        String dictionaryTypeCode = jsonData.getString("dictionaryTypeCode");
        String dictionaryTypeName = jsonData.getString("dictionaryTypeName");
        String isInvalid = jsonData.getString("isInvalid");

        notBlank(dictionaryTypeCode, "类型编码不能为空");
        notBlank(dictionaryTypeName, "类型名称不能为空");
        Integer newIsInvalid = 0;
        DictionaryType dictionarytype = new DictionaryType();
        dictionarytype.setDictionaryTypeCode(dictionaryTypeCode);
        dictionarytype.setDictionaryTypeName(dictionaryTypeName);
//        if("".equals(isInvalid)||isInvalid == null){
//            isInvalid = newIsInvalid.toString();//默认生效
//            if("0".equals(isInvalid)){
//                dictionarytype.setIsInvalid(true);
//        }else{
//                dictionarytype.setIsInvalid(false);
//        }
//        }else{
//            if("0".equals(isInvalid)){
//                dictionarytype.setIsInvalid(true);
//            }else{
//                dictionarytype.setIsInvalid(false);
//            }
//        }
        dictionarytype.setCreateTime(new Date());
        dictionarytype.setCreateUser(currentUser.getUser());
        Integer ret = iDictionaryTypeService.addDictionaryType(dictionarytype);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/delete/{dictionaryTypeCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除字典值类型")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("dictionaryTypeCodes") String dictionaryTypeCodes) {
        notBlank(dictionaryTypeCodes, "错误的字典值类型");
        String split = dictionaryTypeCodes.trim();
        Pattern p = Pattern.compile("\\s*|\t|\r");
        Matcher m = p.matcher(split);
        split = m.replaceAll("");
        String[] codesAarry = split.split(",");
        List<String> codes = Arrays.asList(codesAarry);
        List<DictionaryType> dictionaryTypeList = iDictionaryTypeService.getDictionaryTypeByCode(codes);
        List<DictionaryType> newDictionaryTypeList = new ArrayList<>();
        for(DictionaryType dictionaryType : dictionaryTypeList){
            dictionaryType.setIsInvalid(false);
            dictionaryType.setUpdateUser(currentUser.getUser());
            dictionaryType.setUpdateTime(new Date());
            newDictionaryTypeList.add(dictionaryType);
        }
        iDictionaryTypeService.deleteDictionaryTypeByCode(newDictionaryTypeList,currentUser.getUserCode());

        Integer ret = 0;
       /* for(String code: split) {
            int res = dictionaryTypeService.deleteCraftCategory(code, currentUser.getUserCode());
            if(res > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                        currentUser.getUserCode(), "删除工艺品类" + code);
            }
            ret += res;
        }*/

        return Result.ok(ret);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "update", notes = "更新字典值类型")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody String jsonString) {//DictionaryType dictionaryType
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        String id = jsonData.getString("id");
        long ids = Long.parseLong(id);
        String dictionaryTypeCode = jsonData.getString("dictionaryTypeCode");
        String dictionaryTypeName = jsonData.getString("dictionaryTypeName");

        notBlank(dictionaryTypeCode, "类型编码不能为空");
        notBlank(dictionaryTypeName, "类型名称不能为空");
        notNull(id, "id不能为空");

        DictionaryType dictionaryType = new DictionaryType();
        dictionaryType.setId(ids);
        dictionaryType.setDictionaryTypeCode(dictionaryTypeCode);
        dictionaryType.setDictionaryTypeName(dictionaryTypeName);
        dictionaryType.setUpdateTime(new Date());
        dictionaryType.setUpdateUser(currentUser.getUser());
        Integer ret = iDictionaryTypeService.updateDictionaryType(dictionaryType);
        if(ret != null && ret > 0) {
            operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.UPDATE,
                    currentUser.getUser(), "类型编码" + dictionaryType.getDictionaryTypeCode()
                            + "类型名称" + dictionaryType.getDictionaryTypeName());
        }
        return Result.ok(ret);
    }


    @Override
    public String getModuleCode() {
        return "dicType";
    }
}
