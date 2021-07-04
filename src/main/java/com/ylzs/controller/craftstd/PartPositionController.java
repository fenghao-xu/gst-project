package com.ylzs.controller.craftstd;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.util.*;
import com.ylzs.common.util.pageHelp.PageUtils;
import com.ylzs.entity.craftstd.PartPosition;
import com.ylzs.service.craftstd.impl.DictionaryService;
import com.ylzs.service.staticdata.PartPositionService;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.PartPositionVo;
import com.ylzs.web.OriginController;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @className PartPositionController
 * @Description
 * @Author sky
 * @create 2020-03-04 16:54:14
 */
@RestController
@RequestMapping(value = "/partPosition")
public class PartPositionController extends OriginController {

    @Resource
    private PartPositionService partPositionService;
    @Resource
    private DictionaryService dictionaryService;

    @RequestMapping(value = "/getPartPositionAll",method = RequestMethod.GET)
    @ApiOperation(value = "/getPartPositionAll",notes = "获取所有部件位置数据",httpMethod = "GET",response = Result.class)
    public Result<List<PartPositionVo>> getPartPositionAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                 @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                 @RequestParam(defaultValue = "partType",required = false) String partType){
        HashMap params = new HashMap();
        if(StringUtils.isNotBlank(partType)){
            params.put("params",partType);
        }
        List<PartPositionVo> partPositonDataList = partPositionService.getPartPositonDataList(params);
        List<PartPositionVo> pageList = PageUtils.startPage(partPositonDataList,page,rows);
        return Result.ok(pageList,Long.valueOf(partPositonDataList.size()));
    }
    @RequestMapping(value = "/getPositionDictionaryALL",method = RequestMethod.GET)
    @ApiOperation(value = "/getPositionDictionaryALl",notes = "获取设计部件位置相关字典值",httpMethod = "GET",response = Result.class)
    public Result<JSONObject> getPositionDictionaryALl(){
        List<DictionaryVo> positionDictionary = dictionaryService.getDictoryAll("PostionType");
        List<DictionaryVo> clothesCateGory = dictionaryService.getDictoryAll("clothesCateGory");
        JSONObject resultObjeect = new JSONObject();
        resultObjeect.put("positionDictionary",positionDictionary);
        resultObjeect.put("clothesCateGory",clothesCateGory);
        return Result.ok(resultObjeect);
    }

    @RequestMapping(value = "/searchPartPositionData",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "/searchPartPositionData",notes = "获取所有部件位置数据",httpMethod = "POST",response = Result.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body",dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    public Result<List<PartPositionVo>> searchPartPositionData(HttpServletRequest request) throws Exception{
        String data = IOUtils.toString(request.getInputStream(),request.getCharacterEncoding());
        JSONObject json = JSONObject.parseObject(data);
        HashMap pam = new HashMap();
        if(StringUtils.isNotBlank(json.getString("clothingCode"))){
            pam.put("clothingCode",json.getString("clothingCode"));
        }
        String partType = json.getString("partType");
        if(StringUtils.isNotBlank(partType)){
            pam.put("partType",partType);
        }
        if(StringUtils.isNotBlank(json.getString("params"))){
            pam.put("params",json.getString("params"));
        }
        Integer page = json.getInteger("page");
        if (page == null) {
            Integer.parseInt(Const.DEFAULT_PAGE);
        }
        Integer rows = json.getInteger("rows");
        if(rows ==null){
            rows = Integer.parseInt(Const.DEFAULT_ROWS);
        }
        List<PartPositionVo> partPositonDataList = partPositionService.getPartPositonDataList(pam);
        List<PartPositionVo> pageList = PageUtils.startPage(partPositonDataList,page,rows);
        return Result.ok(pageList,Long.valueOf(partPositonDataList.size()));
    }

    @RequestMapping(value = "/batchSave")
    public String batchSave(String fileName) throws Exception{
        List<PartPosition>  list = ExcelUtil.readExcelToEntity(PartPosition.class,fileName);
        for(PartPosition p: list){
            if(p.getRandomCode() == null){
                p.setRandomCode(SnowflakeIdUtil.generateId());
            }
            if(p.getClothingCategoryCode() != null && p.getClothingCategoryCode().equalsIgnoreCase("NULL")){
                p.setClothingCategoryCode("");
            }

            p.setIsInvalid(false);
        }
        partPositionService.saveBatch(list);
        return "success";
    }
}
