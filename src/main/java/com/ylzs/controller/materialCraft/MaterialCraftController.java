package com.ylzs.controller.materialCraft;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.util.pageHelp.PageUtils;
import com.ylzs.entity.fabricProperty.req.FabricPropertyDataReq;
import com.ylzs.entity.fabricProperty.resp.FabricPropertyDataResp;
import com.ylzs.entity.materialCraft.enums.StatusEnum;
import com.ylzs.entity.materialCraft.req.MaterialCraftReq;
import com.ylzs.entity.materialCraft.req.QueryMaterialCraftReq;
import com.ylzs.entity.materialCraft.resp.ExportMaterialCraftDataResp;
import com.ylzs.entity.materialCraft.resp.MaterialCraftAllDataResp;
import com.ylzs.entity.materialCraft.resp.QueryMaterialCraftDataResp;
import com.ylzs.entity.partCombCraft.req.UpdatePartCombCraftReq;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.fabricProperty.IFabricPropertyDataService;
import com.ylzs.service.fabricProperty.IFabricPropertyService;
import com.ylzs.service.materialCraft.IMaterialCraftService;
import com.ylzs.vo.DictionaryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.ylzs.common.util.Assert.notBlank;

/**
 * @author weikang
 * @Description 材料工艺
 * @Date 2020/3/5
 */
@RestController
@RequestMapping("/materialCraft")
@Api(tags = "材料工艺")
public class MaterialCraftController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialCraftController.class);

    @Resource
    private IMaterialCraftService iMaterialCraftService;

    @Resource
    private IFabricPropertyService propertyService;

    @Resource
    private IDictionaryService dictionaryService;

    @Resource
    private IFabricPropertyDataService propertyDataService;


    @ApiOperation(value = "addDraftMaterialCraft", notes = "新增材料工艺草稿数据", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/addDraftMaterialCraft", method = RequestMethod.POST)
    public Result addDraftMaterialCraft(@RequestBody MaterialCraftReq req) throws Exception {
        return iMaterialCraftService.addDraftMaterialCraft(req);
    }

    @ApiOperation(value = "addPublishMaterialCraft", notes = "新增材料工艺发布数据", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/addPublishMaterialCraft", method = RequestMethod.POST)
    public Result addPublishMaterialCraft(@RequestBody MaterialCraftReq req) throws Exception {
        return iMaterialCraftService.addPublishMaterialCraft(req);
    }


    @ApiOperation(value = "updateMaterialCraftStatus", notes = "修改材料工艺数据状态", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/updateMaterialCraftStatus", method = RequestMethod.POST)
    public Result updateMaterialCraftStatus(@RequestBody UpdatePartCombCraftReq req) throws Exception {
        //删除 草稿
        if (req.getOperateType().intValue() == BusinessConstants.CommonConstant.ONE) {
            if (req.getRandomCode().longValue() == 0L) {
                return Result.ok();
            }
        } else {
            if (req.getRandomCode() == null)
                return Result.build(BusinessConstants.CommonConstant.ZERO, "材料工艺randomCode不能为空");
        }
        return iMaterialCraftService.updateMaterialCraftStatus(req);
    }

    @ApiOperation(value = "selectMaterialCraftChecklist", notes = "获取材料工艺数据清单", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/selectMaterialCraftChecklist", method = RequestMethod.POST)
    public Result selectMaterialCraftChecklist(@RequestBody QueryMaterialCraftReq craftReq) throws Exception {
        List<QueryMaterialCraftDataResp> craftDataResp = iMaterialCraftService.selectMaterialCraftChecklist(craftReq);
        Long size = Long.valueOf(craftDataResp.size());
        craftDataResp = PageUtils.startPage(craftDataResp, craftReq.getPage(), craftReq.getRows());
        return Result.ok(craftDataResp, size);
    }

    @ApiOperation(value = "selectMaterialCraftRulePart", notes = "获取清单页面材料工艺规则", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectMaterialCraftRulePart", method = RequestMethod.GET)
    public Result selectMaterialCraftRulePart(@RequestParam(value = "randomCode") String randomCode) throws Exception {
        notBlank(randomCode, "材料工艺randomCode不能为空");
        return iMaterialCraftService.selectMaterialCraftRulePart(randomCode);
    }

    @ApiOperation(value = "exportMaterialCraftChecklist", notes = "导出材料工艺数据清单", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportMaterialCraftChecklist", method = RequestMethod.GET)
    public Result exportMaterialCraftChecklist(
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "materialCraftKindCodes", required = false) String materialCraftKindCodes,
            @RequestParam(name = "fabricPropertyCodes", required = false) String fabricPropertyCodes,
            @RequestParam(name = "materialCraftCode", required = false) String materialCraftCode,
            @RequestParam(name = "materialCraftName", required = false) String materialCraftName,
            @RequestParam(name = "materialCraftCodeAndName", required = false) String materialCraftCodeAndName,
            HttpServletResponse response) throws Exception {
        QueryMaterialCraftReq craftReq = new QueryMaterialCraftReq();
        craftReq.setStatus(status);
        craftReq.setMaterialCraftKindCodes(materialCraftKindCodes);
        craftReq.setFabricPropertyCodes(fabricPropertyCodes);
        craftReq.setMaterialCraftCode(materialCraftCode);
        craftReq.setMaterialCraftName(materialCraftName);
        craftReq.setMaterialCraftCodeAndName(materialCraftCodeAndName);
        List<QueryMaterialCraftDataResp> craftDataResp = iMaterialCraftService.selectMaterialCraftChecklist(craftReq);
        if(CollUtil.isEmpty(craftDataResp)){
            return Result.ok("无数据导出");
        }
        List<ExportMaterialCraftDataResp> list = parseList(craftDataResp);
        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名
            writer.addHeaderAlias("materialCraftCode", "材料工艺编码");
            writer.addHeaderAlias("materialCraftName", "材料工艺名称");
            writer.addHeaderAlias("materialCraftKindName", "材料类型");
            writer.addHeaderAlias("fabricPropertyNames", "材料属性");
            writer.addHeaderAlias("status", "状态");
            writer.addHeaderAlias("materialVersion", "版本号");
            writer.addHeaderAlias("materialVersionDesc", "版本说明");
            writer.addHeaderAlias("releaseUser", "版本创建人");
            writer.write(list, true);

            String fileName = URLEncoder.encode("材料工艺清单.xls", "UTF-8");
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

    @ApiOperation(value = "selectMaterialCraftData", notes = "获取材料工艺数据", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectMaterialCraftData", method = RequestMethod.GET)
    public Result selectMaterialCraftData(@RequestParam(value = "randomCode") String randomCode) throws Exception {
        notBlank(randomCode, "材料工艺randomCode不能为空");
        return iMaterialCraftService.selectMaterialCraftData(randomCode);
    }

    @ApiOperation(value = "selectFabricProperty", notes = "获取材料属性", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectFabricProperty", method = RequestMethod.GET)
    public Result selectFabricProperty(@RequestParam(value = "kindCode") String kindCode) throws Exception {
        notBlank(kindCode, "材料类型编码不能为空");
        return propertyService.getFabricProperty(kindCode);
    }

    @ApiOperation(value = "selectMaterialTypeData", notes = "获取材料类型值", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectMaterialTypeData", method = RequestMethod.GET)
    public Result selectMaterialTypeData() throws Exception {
        List<DictionaryVo> materialType = dictionaryService.getDictoryAll("MaterialType");
        return Result.ok(materialType);
    }

    @ApiOperation(value = "selectFabricPropertyData", notes = "获取属性值", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/selectFabricPropertyData", method = RequestMethod.POST)
    public Result selectFabricPropertyData(@RequestBody FabricPropertyDataReq req) throws Exception {
        if(StringUtils.isBlank(req.getFabricPropertyCode())){
            return Result.build(BusinessConstants.CommonConstant.ZERO, "材料属性编码不能空");
        }
        if(StringUtils.isBlank(req.getKindCode())){
            return Result.build(BusinessConstants.CommonConstant.ZERO, "材料类型编码不能空");
        }
        List<FabricPropertyDataResp> list = propertyDataService.selectPropertyDataList(req.getFabricPropertyCode(), req.getPropertyValue(),req.getKindCode());
        Long size = Long.valueOf(list.size());
        list = PageUtils.startPage(list, req.getPage(), req.getRows());
        return Result.ok(list, size);
    }

    private List<ExportMaterialCraftDataResp> parseList(List<QueryMaterialCraftDataResp> craftDataResp) {
        List<ExportMaterialCraftDataResp> list = new ArrayList<>();
        for (QueryMaterialCraftDataResp resp : craftDataResp) {
            ExportMaterialCraftDataResp export = new ExportMaterialCraftDataResp();
            export.setFabricPropertyNames(resp.getFabricPropertyNames());
            export.setMaterialCraftCode(resp.getMaterialCraftCode());
            export.setMaterialCraftKindName(resp.getMaterialCraftKindName());
            export.setMaterialCraftName(resp.getMaterialCraftName());
            export.setMaterialVersion(resp.getMaterialVersion());
            export.setMaterialVersionDesc(resp.getMaterialVersionDesc());
            export.setStatus(StatusEnum.parse(resp.getStatus()));
            export.setReleaseUser(resp.getCreateUser());
            list.add(export);
        }
        return list;
    }

    @RequestMapping(value = "/selectMaterialCraftDataByKindCode", method = RequestMethod.GET)
    @ApiOperation(value = "selectMaterialCraftDataByKindCode", notes = "获取数据", httpMethod = "GET", response = Result.class)
    public Result selectMaterialCraftDataByKindCode(String code) throws Exception {
        List<MaterialCraftAllDataResp> resps = iMaterialCraftService.selectMaterialCraftDataByKindCode(code);
        return Result.ok(resps);
    }


}
