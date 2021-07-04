package com.ylzs.controller.craftstd;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.AuthenticationInterceptor;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.craftstd.Line;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.craftstd.ILineService;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.vo.DictionaryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.notBlank;
import static com.ylzs.common.util.Assert.notNull;

/**
 * 说明：字典
 *
 * @author lyq
 * 2019-10-22 15:20
 */
@Api(tags = "字典")
@RestController
@RequestMapping(value = "/dic")
public class DictionaryController implements IModuleInfo {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Resource
    IDictionaryService dictionaryService;
    @Resource
    IOperationLogService operationLogService;

    @Resource
    ILineService lineService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/getDictionaryByParent", method = RequestMethod.GET)
    @ApiOperation(value = "getDictionaryByParent", notes = "查询子字典")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getDictionaryByParent(@RequestParam(name = "parentCode", required = true) String parentCode,
                                           @RequestParam(name = "parentId", required = false) Integer parentId) {
        notBlank(parentCode, "字典类型代码不能为空");
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent(parentCode, parentId);
        return Result.ok(dictionarys);
    }

    @RequestMapping(value = "/getDictionaryByParentId", method = RequestMethod.GET)
    @ApiOperation(value = "getDictionaryByParentId", notes = "查询字典值")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getDictionaryByParentId(
            @RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
            @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
            @RequestParam(name = "parentId", required = true) Integer parentId,
            @RequestParam(name = "keywords", required = false) String keywords,
            @RequestParam(name = "beginDate", required = false) Date beginDate,
            @RequestParam(name = "endDate", required = false) Date endDate) {
        //List<Dictionary> dictionarys = dictionaryService.getDictionaryByParentId(parentId);
        //return Result.ok(dictionarys);
        notNull(parentId, "上级id不能为空");
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByPage(parentId,
                keywords,beginDate,endDate);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getOne/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询指定字典")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getOne(@PathVariable("id") Integer id) {
        notNull(id, "id不能为空");
        List<Dictionary> dictionaries = dictionaryService.getDictionaryById(id);
        return Result.ok(dictionaries);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部字典")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                           @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                           @RequestParam(name = "keywords", required = false) String keywords,
                                           @RequestParam(name = "beginDate", required = false) Date beginDate,
                                           @RequestParam(name = "endDate", required = false) Date endDate) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByPage(null,
                keywords,beginDate,endDate);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }



    @RequestMapping(value = "/getAllRole", method = RequestMethod.GET)
    @ApiOperation(value = "getAllRole", notes = "查询全部角色")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllRole(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                           @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent("Role", null);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllModule", method = RequestMethod.GET)
    @ApiOperation(value = "getAllModule", notes = "查询全部模块")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllModule(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                               @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent("Module", null);
        if(dictionarys != null && dictionarys.size() > 0) {
            for (Dictionary dictionary: dictionarys) {
                dictionary.setChildrens(dictionaryService.getDictionaryByParentId(Integer.parseInt(String.valueOf(dictionary.getId()))));
            }
        }
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllLinePosition", method = RequestMethod.GET)
    @ApiOperation(value = "getAllLinePosition", notes = "查询全部用线部位线名")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllLinePosition(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                 @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent("LinePosition", null);
        for(Dictionary dic: dictionarys) {
            List<Dictionary> dics = dictionaryService.getDictionaryByParentId(Integer.parseInt(String.valueOf(dic.getId())));
            dic.setChildrens(dics);
            if(dics != null && !dics.isEmpty()) {
                for (Dictionary dic1: dic.getChildrens()) {
                    List<Dictionary> dics1 = dictionaryService.getDictionaryByParentId(Integer.parseInt(String.valueOf(dic1.getId())));
                    dic1.setChildrens(dics1);
                    if (dics1 != null && !dics1.isEmpty()) {
                        for (Dictionary dic2: dic1.getChildrens()) {
                            List<Dictionary> dics2 = dictionaryService.getDictionaryByParentId(Integer.parseInt(String.valueOf(dic2.getId())));
                            dic2.setChildrens(dics2);
                        }
                    }

                }
            }
        }
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllLinePositionEx", method = RequestMethod.GET)
    @ApiOperation(value = "getAllLinePositionEx", notes = "查询全部用线部位线名线型")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllLinePositionEx(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                       @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        Dictionary lastPosition = null;
        Dictionary lastName = null;
        List<Line> lines = lineService.getAllLine();
        List<Dictionary> dictionarys = new ArrayList<Dictionary>();
        for (Line line: lines) {
            if(lastPosition == null || (line.getLinePosition() != null && lastPosition.getId() != line.getLinePosition().getId())) {
                dictionarys.add(line.getLinePosition());
                lastPosition = dictionarys.get(dictionarys.size() - 1);
                if(lastPosition.getChildrens() == null) {
                    List<Dictionary> dics = new ArrayList<Dictionary>();
                    lastPosition.setChildrens(dics);
                }

            }
            if(lastName == null || (line.getLineNameObj() != null && lastName.getId() != line.getLineNameObj().getId())) {
                lastPosition.getChildrens().add(line.getLineNameObj());
                lastName = lastPosition.getChildrens().get(lastPosition.getChildrens().size() - 1);
                if(lastName.getChildrens() == null) {
                    List<Dictionary> dics = new ArrayList<Dictionary>();
                    lastName.setChildrens(dics);
                }
            }

            Dictionary dic = new Dictionary();
            dic.setId(line.getId());
            dic.setDicValue(line.getLineCode());
            dic.setValueDesc(line.getLineName());
            lastName.getChildrens().add(dic);
        }

        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }




    @RequestMapping(value = "/getAllLineName", method = RequestMethod.GET)
    @ApiOperation(value = "getAllLineName", notes = "查询全部线名")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllLineName(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                       @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent("LineName", null);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllDevice", method = RequestMethod.GET)
    @ApiOperation(value = "getAllDevice", notes = "查询全部设备")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllDevice(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                   @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent("Device", null);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }


    @RequestMapping(value = "/getAllClothesCategory", method = RequestMethod.GET)
    @ApiOperation(value = "getAllClothesCategory", notes = "查询全部服装品类")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllClothesCategory(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                   @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent("ClothesCategory", null);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllSection", method = RequestMethod.GET)
    @ApiOperation(value = "getAllSection", notes = "查询全部工段")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllSection(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                          @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent("Section", null);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllWorkType", method = RequestMethod.GET)
    @ApiOperation(value = "getAllWorkType", notes = "查询全部工种")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<Dictionary>> getAllWorkType(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                  @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<Dictionary> dictionarys = dictionaryService.getDictionaryByParent("WorkType", null);
        PageInfo<Dictionary> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllAssistanceToolType", method = RequestMethod.GET)
    @ApiOperation(value = "getAllAssistanceToolType", notes = "查询全部辅助工具类型")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<DictionaryVo>> getAllAssistanceToolType(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                   @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        PageHelper.startPage(page, rows);
        List<DictionaryVo> dictionarys = dictionaryService.getDictoryAll("AssistanceToolType");
        PageInfo<DictionaryVo> pageInfo = new PageInfo<>(dictionarys);
        return Result.ok(dictionarys, pageInfo.getTotal());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加字典")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody String jsonString) { //Dictionary dictionary
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        String dicValue = jsonData.getString("dicValue");
        String valueDesc = jsonData.getString("valueDesc");
        Integer seqNum = jsonData.getInteger("seqNum");
        Integer parentId = jsonData.getInteger("parentId");
        String remark = jsonData.getString("remark");

        notBlank(dicValue, "字典值代码不能为空");
        notBlank(dicValue, "字典值名称不能为空");

        Dictionary dictionary = new Dictionary();
        dictionary.setDicValue(dicValue);
        dictionary.setValueDesc(valueDesc);
        dictionary.setSeqNum(seqNum);
        dictionary.setParentId(parentId);
        dictionary.setRemark(remark);
        dictionary.setUpdateTime(new Date());
        dictionary.setUpdateUser(currentUser.getUser());
        Integer ret = dictionaryService.addDictionary(dictionary);
        if(ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除字典")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("ids") String ids) {
        notBlank(ids, "id不能为空");
        Integer ret = 0;
        String[] split = ids.split(",");
        for (String itm: split) {
            Integer id = Integer.parseInt(itm);
            List<Dictionary> dictionaries = dictionaryService.getDictionaryById(id);
            for(Dictionary dictionary: dictionaries) {
                dictionary.setInvalid(true);
                //Integer res = dictionaryService.deleteDictionary(dictionary.getId());
                Integer res = dictionaryService.updateDictionary(dictionary);
                if (res != null && res > 0) {
                    operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.DELETE,
                            currentUser.getUser(), "字典值" + dictionary.getDicValue()
                                    + "字黄值描述" + dictionary.getValueDesc());
                }
                ret += res;
            }
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新字典")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody String jsonString) {//Dictionary dictionary
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        Integer id = jsonData.getInteger("id");
        String dicValue = jsonData.getString("dicValue");
        String valueDesc = jsonData.getString("valueDesc");
        Integer seqNum = jsonData.getInteger("seqNum");
        Integer parentId = jsonData.getInteger("parentId");
        String remark = jsonData.getString("remark");

        notBlank(dicValue, "字典值代码不能为空");
        notBlank(dicValue, "字典值名称不能为空");
        notNull(id, "id不能为空");

        Dictionary dictionary = new Dictionary();
        dictionary.setId(id);
        dictionary.setDicValue(dicValue);
        dictionary.setValueDesc(valueDesc);
        dictionary.setSeqNum(seqNum);
        dictionary.setParentId(parentId);
        dictionary.setRemark(remark);


        dictionary.setUpdateTime(new Date());
        dictionary.setUpdateUser(currentUser.getUser());
        Integer ret = dictionaryService.updateDictionary(dictionary);
        if(ret != null && ret > 0) {
            operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.UPDATE,
                    currentUser.getUser(), "字典值" + dictionary.getDicValue()
                            + "字黄值描述" + dictionary.getValueDesc());
        }
        return Result.ok(ret);
    }


    @Override
    public String getModuleCode() {
        return "dic";
    }

    @RequestMapping(value = "/getDictoryAll",method = RequestMethod.GET)
    @ApiOperation(value = "getDictoryAll",notes = "根据字典类型获取字典数据",response = Result.class)
    public Result<List<DictionaryVo>>getDictoryAll(@RequestParam String dictoryTypeCode) {

        if(StringUtils.isEmpty(dictoryTypeCode)){
            return Result.fail(MessageConstant.MissingServletRequestParameter);
        }
        List<DictionaryVo> list = new ArrayList<>();
        try {
            list = dictionaryService.getDictoryAll(dictoryTypeCode);
            if(list.isEmpty()){
                throw  new RuntimeException();
            }

        }catch (Exception ex){
            ex.printStackTrace();
            return Result.fail(MessageConstant.SERVER_FIELD_ERROR);
        }
        return Result.ok(list);
    }
}
