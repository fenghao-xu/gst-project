package com.ylzs.controller.designPart;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.*;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.entity.designPart.DesignPart;
import com.ylzs.entity.partThesaurus.PartMiddle;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.designPart.IDesignPartService;
import com.ylzs.service.partThesaurus.IPartMiddleService;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.designpart.DesignPartExportVo;
import com.ylzs.vo.designpart.DesignPartSourceVo;
import com.ylzs.vo.designpart.DesignPartVo;
import com.ylzs.web.OriginController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;


/**
 * 设计部件
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 11:53:47
 */
@RestController
@RequestMapping(value = "/designPart")
//@ApiModel(value = "设计部件web层")
@Api(tags = "设计部件")
public class DesignPartController extends OriginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DesignPartController.class);

    @Autowired
    private IDesignPartService designPartService;
    @Resource
    private IDictionaryService dictionaryService;
    @Resource
    private IPartMiddleService partMiddleService;


    @RequestMapping(value = "/getDropInfo", method = RequestMethod.GET)
    @ApiOperation(value = "getDropInfo", notes = "获取下拉信息")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result getDropInfo() {
        List<PartMiddle> partMiddles = partMiddleService.list();
        List<DictionaryVo> clothesCategorys = dictionaryService.getDictoryAll("ClothesCategory");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("partMiddles", partMiddles);
        jsonObject.put("clothesCategorys", clothesCategorys);

        return Result.ok(jsonObject);
    }

    @RequestMapping(value = "/searchDesignPartData",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "/searchDesignPartData",notes = "查询设计部件信息",response = Result.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body",dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    public Result<List<DesignPartVo>> searchDesignPartData(HttpServletRequest request)throws Exception{

        String data = IOUtils.toString(request.getInputStream(),request.getCharacterEncoding());
        JSONObject json = JSONObject.parseObject(data);
        HashMap pam = new HashMap();
        if(StringUtils.isNotBlank(json.getString("clothingCode"))){
            pam.put("clothingCode",json.getString("clothingCode"));
        }
        if(StringUtils.isNotBlank(json.getString("partMiddleCode"))){
            pam.put("partMiddleCode",json.getString("partMiddleCode"));
        }
        if(StringUtils.isNotBlank(json.getString("param"))){
            pam.put("params",json.getString("param"));
        }
        Integer page = json.getInteger("page");
        if (page == null) {
            page = Integer.parseInt(Const.DEFAULT_PAGE);
        }
        Integer rows = json.getInteger("rows");
        if(rows ==null){
            rows = Integer.parseInt(Const.DEFAULT_ROWS);
        }
        if(StringUtils.isNotBlank(json.getString("general"))){
            pam.put("general",json.getString("general"));
        }
        String flag = json.getString("flag");

        List<DesignPartVo> designPartVos;
        if (!"notpage".equalsIgnoreCase(flag)) {
            PageHelper.startPage(page, rows);
            designPartVos = designPartService.getDesignDataList(pam);
            PageInfo<DesignPartVo> pageInfo = new PageInfo<>(designPartVos);
            return Result.ok(designPartVos, pageInfo.getTotal());
        } else {
            designPartVos = designPartService.getDesignDataList(pam);
        }
        Long totalSize = Long.valueOf(designPartVos.size());
        return Result.ok(designPartVos, totalSize);
    }



    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询设计部件信息", httpMethod = "GET",  response = Result.class)
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clothingCode",value = "服装品类编码",dataType = "String",required = false),
            @ApiImplicitParam(name = "partMiddleCode",value = "部件中类编码",dataType = "String",required = false),
            @ApiImplicitParam(name = "beginDate",value = "开始日期",dataType = "String",required = false),
            @ApiImplicitParam(name = "endDate",value = "结束日期",dataType = "String",required = false),
            @ApiImplicitParam(name = "params",value = "关键字",dataType = "String",required = false),
            @ApiImplicitParam(name = "designPartCode",value = "设计部件编码",dataType = "String",required = false),
            @ApiImplicitParam(name = "designPartCode",value = "设计部件名称",dataType = "String",required = false),

    })
    public Result<List<DesignPartVo>> getAll(
            @RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
            @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
            @RequestParam(name = "clothingCode", required = false) String clothingCode,
            @RequestParam(name = "partMiddleCode", required = false) String partMiddleCode,
            @RequestParam(name = "beginDate", required = false) String beginDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "param", required = false) String param,
            @RequestParam(name = "general", required = false) String general,
            @RequestParam(name = "flag", required = false) String flag,
            @RequestParam(name = "designPartCode", required = false) String designPartCode,
            @RequestParam(name = "designPartName", required = false) String designPartName

       ) throws Exception {

        HashMap pam = new HashMap();
        if (StringUtils.isNotBlank(clothingCode)) {
            pam.put("clothingCode", clothingCode);
        }
        if (StringUtils.isNotBlank(partMiddleCode)) {
            pam.put("partMiddleCode", partMiddleCode);
        }
        if(StringUtils.isNotBlank(beginDate)) {
            pam.put("beginDate", beginDate);
        }
        if(StringUtils.isNotBlank(endDate)) {
            pam.put("endDate", endDate);
        }
        if (StringUtils.isNotBlank(param)) {
            pam.put("params", param);
        }

        if (StringUtils.isNotBlank(general)) {
            pam.put("general", general);
        }

        if(StringUtils.isNotBlank(designPartCode)) {
            pam.put("designPartCode", designPartCode);
        }
        if(StringUtils.isNotBlank(designPartName)) {
            pam.put("designPartName", designPartName);
        }


        List<DesignPartVo> designPartVos;
        if (!"notpage".equalsIgnoreCase(flag)) {
            PageHelper.startPage(page, rows);
            designPartVos = designPartService.getDesignDataList(pam);
            PageInfo<DesignPartVo> pageInfo = new PageInfo<>(designPartVos);
            return Result.ok(designPartVos, pageInfo.getTotal());
        } else {
            designPartVos = designPartService.getDesignDataList(pam);
        }
        Long totalSize = Long.valueOf(designPartVos.size());
        return Result.ok(designPartVos, totalSize);
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "export", notes = "导出设计部件信息", httpMethod = "GET",  response = Result.class)
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<DesignPartVo>> export(@RequestParam(name = "categoryCode", required = false) String clothingCode,
                                             @RequestParam(name = "partMiddleCode", required = false) String partMiddleCode,
                                             @RequestParam(name = "beginDate", required = false) String beginDate,
                                             @RequestParam(name = "endDate", required = false) String endDate,
                                             @RequestParam(name = "params", required = false) String params,
                                             HttpServletResponse response) throws Exception {

        HashMap pam = new HashMap();
        if (StringUtils.isNotBlank(clothingCode)) {
            pam.put("clothingCode", clothingCode);
        }
        if (StringUtils.isNotBlank(partMiddleCode)) {
            pam.put("partMiddleCode", partMiddleCode);
        }
        if(StringUtils.isNotBlank(beginDate)) {
            pam.put("beginDate", beginDate);
        }
        if(StringUtils.isNotBlank(endDate)) {
            pam.put("endDate", endDate);
        }
        if (StringUtils.isNotBlank(params)) {
            pam.put("params", params);
        }

        List<DesignPartExportVo> designPartVos = designPartService.getDesignDataListExport(pam);
        //通过工具类创建writer
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名
            writer.addHeaderAlias("designCode", "设计部件编码");
            writer.addHeaderAlias("designName", "设计部件名称");
            writer.addHeaderAlias("designImage", "设计部件图案");
            writer.addHeaderAlias("clothingCategory", "服装品类编码");
            writer.addHeaderAlias("craftCategoryName", "服装品类名称");
            writer.addHeaderAlias("partMiddleCode", "部件中类编码");
            writer.addHeaderAlias("partMiddleName", "部件中类名称");
            writer.addHeaderAlias("partPosition", "部件位置");
            writer.addHeaderAlias("patternType", "图案类型");
            writer.addHeaderAlias("patternMode", "图案方式");
            writer.addHeaderAlias("gongYiExplain", "工艺说明");

            writer.addHeaderAlias("patternTechnologyD", "图案工艺编码");
            writer.addHeaderAlias("patternTechnology", "图案工艺");
            writer.addHeaderAlias("patternMsg", "图案线色说明");
            writer.addHeaderAlias("affectCraft", "是否影响工艺");
            writer.addHeaderAlias("createUser", "创建人");
            writer.addHeaderAlias("createTime", "创建时间");
            writer.addHeaderAlias("receiveTime", "接收时间");
            writer.write(designPartVos, true);

            String fileName = URLEncoder.encode("设计部件清单.xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            LOGGER.error("designpart export fails", e);
        } finally {
            writer.close();
            if (null != out) {
                IoUtil.close(out);
            }
        }
        return null;
    }

    @RequestMapping(value = "/getDesignPartSourceVos", method = RequestMethod.GET)
    @ApiOperation(value = "getDesignPartSourceVos", notes = "查询设计部件使用情况", httpMethod = "GET",  response = Result.class)
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    @ApiImplicitParams({@ApiImplicitParam(name = "designPartCode",value = "设计部件编码",dataType = "String",required = false),
            @ApiImplicitParam(name = "useIn",value = "使用类型 0部件工艺 1智库款工艺 2订单工艺路线",dataType = "Integer",required = false)})
    public Result<List<DesignPartSourceVo>> getDesignPartSourceVos(@RequestParam(name = "designPartCode", required = true) String designPartCode,
            @RequestParam(name = "useIn", required = false, defaultValue = "0") Integer useIn) {
        List<DesignPartSourceVo> lst = designPartService.getDesignPartSourceVos(designPartCode, useIn);
        return Result.ok(lst);
    }



    @RequestMapping(value = "/batchSaveDesign")
    public String batchSaveDesign(String fileName) throws Exception {
        List<DesignPart> list = ExcelUtil.readExcelToEntity(DesignPart.class, fileName);
        for (DesignPart part : list) {
            part.setRandomCode(SnowflakeIdUtil.generateId());
            if (StringUtils.isEmpty(part.getPartPosition())) {
                part.setPartPosition("");
            }
            if (StringUtils.isEmpty(part.getPatternType())) {
                part.setPatternType("");
            }

            if (StringUtils.isEmpty(part.getPatternMode())) {
                part.setPatternMode("");
            }
        }
        designPartService.saveBatch(list);
        return "success";
    }


}
