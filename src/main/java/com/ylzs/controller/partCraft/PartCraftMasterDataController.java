package com.ylzs.controller.partCraft;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.PartCraftPartType;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.util.pageHelp.PageUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.partCraft.PartCraftMasterData;
import com.ylzs.entity.plm.PICustomOrder;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.custom.ICustomStyleCraftCourseService;
import com.ylzs.service.partCraft.*;
import com.ylzs.service.plm.IPICustomOrderService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import com.ylzs.vo.CraftCateGoryVo;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.SewingCraftVo;
import com.ylzs.vo.clothes.ClothesVo;
import com.ylzs.vo.partCraft.*;
import com.ylzs.web.OriginController;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.ylzs.common.util.Assert.isFalse;
import static com.ylzs.common.util.Assert.notNull;


/**
 * 部件工艺主数据表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Transactional(rollbackFor = Exception.class)
@RestController
@ApiModel(description = "部件工艺主数据web层")
@RequestMapping(value = "/partCraft")
public class PartCraftMasterDataController extends OriginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartCraftMasterDataController.class);

    @Autowired
    private IPartCraftMasterDataService partCraftMasterDataService;
    @Resource
    private ICraftCategoryService craftCategoryService;
    @Resource
    private IDictionaryService dictionaryService;
    @Resource
    private SewingCraftWarehouseService sewingCraftWarehouseService;
    @Resource
    private IPartCraftDetailService partCraftDetailService;
    @Autowired
    private IPartCraftMasterPictureService partCraftMasterPictureService;
    @Autowired
    private IPartCraftRuleService partCraftRuleService;
    @Autowired
    private IPartCraftDesignPartsService partCraftDesignPartsService;
    @Autowired
    private IPartCraftPositionService positionService;

    @Resource
    private ICraftMainFrameService craftMainFrameService;

    @Resource
    IPICustomOrderService ipiCustomOrderService;

    @Resource
    ICustomStyleCraftCourseService customStyleCraftCourseService;
    @Resource
    ThreadPoolTaskExecutor taskExecutor;


    @RequestMapping(value = "/getPartCraftData")
    @ApiOperation(value = "/getPartCraftData", notes = "获取部件工艺基础数据", httpMethod = "GET", response = Result.class)
    @Authentication(auth = Authentication.AuthType.QUERY)
    public Result<JSONObject> getPartCraftData(HttpServletRequest request, @RequestParam String dictoryTypeCode) {
        if (dictoryTypeCode.isEmpty()) {
            throw new RuntimeException();
        }
        String contextPath = request.getContextPath();
        JSONObject jsonObject = new JSONObject();
        //包含工艺品类，结构部件
        List<CraftCateGoryVo> craftCategories = craftCategoryService.getCraftCategoryApartVos();
        Integer ins[] = new Integer[craftCategories.size()];
        for (int i = 0; i < craftCategories.size(); i++) {
            ins[i] = craftCategories.get(i).getId();
        }
        List<ClothesVo> clothesVos = craftCategoryService.getClothesAll(ins);
        Map<Integer, List<ClothesVo>> groupbyClothesList = clothesVos.stream().collect(
                Collectors.groupingBy(ClothesVo::getCraftCategoryId));
        for (Integer id : groupbyClothesList.keySet()) {
            for (CraftCateGoryVo vo : craftCategories) {
                if (id.equals(vo.getId())) {
                    vo.setClothesVos(groupbyClothesList.get(id));
                }
            }
        }

        jsonObject.put("craftCategory", craftCategories);
        List<DictionaryVo> dictoryAll = dictionaryService.getDictoryAll(dictoryTypeCode);
        jsonObject.put("dictionary", dictoryAll);

        return Result.ok(jsonObject);
    }

    @RequestMapping(value = "getCraftWareAll", method = RequestMethod.GET)
    @ApiOperation(value = "/getCraftWareAll", notes = "获取所有车缝工序数据", httpMethod = "GET")
    @Authentication(auth = Authentication.AuthType.QUERY)
    public Result<List<SewingCraftVo>> getCraftWareAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                       @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                       @RequestParam String categoryCode) {
        HashMap param = new HashMap();
        if (StringUtils.isNotEmpty(categoryCode)) param.put("categoryCode", categoryCode);
        List<SewingCraftVo> sewingCraftVos = sewingCraftWarehouseService.searchSewingCraftData(param);
        Long totalSize = null;
        if (ObjectUtils.isNotEmptyList(sewingCraftVos)) {
            totalSize = Long.valueOf(sewingCraftVos.size());
            sewingCraftVos = PageUtils.startPage(sewingCraftVos, page, rows);
        }
        return Result.ok(sewingCraftVos, totalSize);
    }

    @RequestMapping(value = "/getPartCraftInfoAll")
    @ApiOperation(value = "/getPartCraftInfoAll", notes = "获取部件工艺数据", httpMethod = "POST", response = Result.class)
    @Authentication(auth = Authentication.AuthType.QUERY)
    public Result<List<PartCraftMasterBasicVo>> getPartCraftInfoAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                                    @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows) {
        List<PartCraftMasterBasicVo> list = partCraftMasterDataService.searchPartCraftInfo(new HashMap());
        Long total = null;
        if (ObjectUtils.isNotEmptyList(list)) {
            total = Long.valueOf(list.size());
            list = PageUtils.startPage(list, page, rows);
        }
        return Result.ok(list, total);
    }

    @RequestMapping(value = "/flushCraftFlowNum", method = RequestMethod.POST)
    @ApiOperation(value = "/flushCraftFlowNum", notes = "获取部件工艺数据")
    public Result<List<JSONObject>> flushCraftFlowNum(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        JSONObject dataObj = JSONObject.parseObject(data);
        String businessType = dataObj.getString("businessType");
        String craftCategoryCode = dataObj.getString("craftCategoryCode");
        String codeList = dataObj.getString("codeList");
        if (StringUtils.isEmpty(codeList)) {
            return Result.build(MessageConstant.PARAM_NULL, "codeList参数为空");
        }
        JSONArray codeArray = JSONArray.parseArray(codeList);

        List<CraftMainFrame> craftMainFrames = craftMainFrameService.getByCraftCategoryAndType(craftCategoryCode, businessType);
        if (null == craftMainFrames || craftMainFrames.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "没有找到合适的主框架");
        }
        CraftMainFrame mainFrame = craftMainFrames.get(0);
        //大货取默认的
        if ("大货".equals(businessType)) {
            for (CraftMainFrame frame : craftMainFrames) {
                if (Boolean.TRUE.equals(frame.getIsDefault())) {
                    mainFrame = frame;
                    break;
                }
            }
        }
        List<String> craftCodeList = new ArrayList<>();
        for (int i = 0; i < codeArray.size(); i++) {
            JSONObject obj = codeArray.getJSONObject(i);
            String code = obj.getString("code");
            craftCodeList.add(code);
        }
        List<JSONObject> result = new ArrayList<>();
        Map<String, SewingCraftWarehouse> map = sewingCraftWarehouseService.getCraftFlowNumByCraftCodeAndMainFrameCode(craftCodeList, mainFrame.getMainFrameCode());
        if (null != map && map.size() > 0) {
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                JSONObject obj = new JSONObject();
                obj.put("code", key);
                obj.put("craftFlowNum", map.get(key).getCraftFlowNum());
                result.add(obj);
            }
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/searchPartCraftInfo", method = RequestMethod.GET)
    @ApiOperation(value = "/searchPartCraftInfo", notes = "根据条件查询部件工艺信息", httpMethod = "GET", response = Result.class)
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<PartCraftMasterBasicVo>> searchPartCraftInfo(HttpServletRequest request,
                                                                    @RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                                    @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                                    @RequestParam(name = "categoryCode", required = false) String categoryCode,
                                                                    @RequestParam(name = "craftPartCode", required = false) String craftPartCode,
                                                                    @RequestParam(name = "defaultStatus", required = false) String defaultStatus,
                                                                    @RequestParam(name = "queryData", required = false) String queryData) {
        HashMap params = new HashMap();
        if (StringUtils.isNotBlank(defaultStatus)) {
            params.put("defaultStatus", defaultStatus);
        }
        if (StringUtils.isNotBlank(queryData)) {
            params.put("queryData", queryData);
        }
        if (StringUtils.isNotBlank(craftPartCode)) {
            params.put("craftPartCode", craftPartCode);
        }
        if (StringUtils.isNotBlank(categoryCode)) {
            params.put("categoryCode", categoryCode);
        }
        PageHelper.startPage(page, rows);
        List<PartCraftMasterBasicVo> list = partCraftMasterDataService.searchPartCraftInfo(params);
        PageInfo<PartCraftMasterBasicVo> pageInfo = new PageInfo<>(list);
        return Result.ok(list, pageInfo.getTotal());
    }


    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "export", notes = "导出部件工艺信息", httpMethod = "GET", response = Result.class)
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<PartCraftMasterBasicVo>> export(@RequestParam(name = "categoryCode", required = false) String categoryCode,
                                                       @RequestParam(name = "craftPartCode", required = false) String craftPartCode,
                                                       @RequestParam(name = "defaultStatus", required = false) String defaultStatus,
                                                       @RequestParam(name = "queryData", required = false) String queryData,
                                                       HttpServletResponse response) {
        HashMap params = new HashMap();
        if (StringUtils.isNotBlank(defaultStatus)) {
            params.put("defaultStatus", defaultStatus);
        }
        if (StringUtils.isNotBlank(queryData)) {
            params.put("queryData", queryData);
        }
        if (StringUtils.isNotBlank(craftPartCode)) {
            params.put("craftPartCode", craftPartCode);
        }
        if (StringUtils.isNotBlank(categoryCode)) {
            params.put("categoryCode", categoryCode);
        }
        List<PartCraftMasterExportVo> list = partCraftMasterDataService.searchPartCraftInfoExport(params);

        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名
            writer.addHeaderAlias("partCraftMainCode", "部件工艺编码");
            writer.addHeaderAlias("partCraftMainName", "部件工艺名称");
            writer.addHeaderAlias("craftCategoryCode", "工艺品类编码");
            writer.addHeaderAlias("craftCategoryName", "工艺品类名称");
            writer.addHeaderAlias("craftPartCode", "结构部件编码");
            writer.addHeaderAlias("craftPartName", "结构部件名称");
            writer.addHeaderAlias("partType", "部件类型");
            writer.addHeaderAlias("standardTime", "标准时间");
            writer.addHeaderAlias("standardPrice", "标准单价");
            writer.addHeaderAlias("createUser", "创建人");
            writer.addHeaderAlias("createTime", "创建时间");

            writer.addHeaderAlias("updateUser", "维护人");
            writer.addHeaderAlias("updateTime", "维护时间");
            writer.addHeaderAlias("releaseUser", "发布人");
            writer.addHeaderAlias("releaseTime", "发布时间");


            writer.write(list, true);

            String fileName = URLEncoder.encode("部件工艺清单.xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            LOGGER.error("bigStyleRecord export fails", e);
        } finally {
            writer.close();
            if (null != out) {
                IoUtil.close(out);
            }
        }
        return null;

    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "/add", notes = "新增部件工艺", httpMethod = "POST", response = Result.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    @Authentication(auth = Authentication.AuthType.INSERT, required = true)
    public Result add(HttpServletRequest request,
                      @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        PartCraftMasterBasicVo basicVo = JSON.parseObject(data, PartCraftMasterBasicVo.class);
        if (!checkBasicData(basicVo)) {
            return Result.build(MessageConstant.MissingServletRequestParameter, MessageConstant.STATUS_TEXT_DATA_NULL);
        }
        basicVo.setCreateUser(currentUser.getUserName());
        basicVo.setReleaseUser(null);
        basicVo.setReleaseTime(null);
        Result result = getCheckParment(basicVo);
        if (result.getCode() != 1) return result;
        if (partCraftMasterDataService.isPartCraftNameUsed(basicVo.getRandomCode(), basicVo.getCraftCategoryCode(), basicVo.getPartCraftMainName())) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "部件名称重复");

        }

        PartCraftMasterData masterData = partCraftMasterDataService.savePartCraftData(basicVo);
        if (masterData == null) {
            return Result.build(MessageConstant.MissingServletRequestParameter, MessageConstant.STATUS_TEXT_INTERNAL_ERROR);
        }
        HashMap params = new HashMap();
        params.put("randomCode", masterData.getRandomCode());
        params.put("status", masterData.getStatus());
        PartCraftMasterBasicVo basicVos = partCraftMasterDataService.searchPartCraftInfoRandomCode(params);
        JSONObject json = new JSONObject();
        json.put("partCraftMaster", basicVos);
        json.put("partCraftDetails", partCraftDetailService.getPartCraftDetailMainList(masterData.getRandomCode(), masterData.getStatus()));
        json.put("craftDesignParts", partCraftDesignPartsService.getDesignPartsMainList(masterData.getRandomCode(), masterData.getStatus()));
        json.put("craftPositions", positionService.getPartCraftPositionMainList(masterData.getRandomCode(), masterData.getStatus()));
        json.put("partCraftRules", partCraftRuleService.getRulesList(masterData.getRandomCode(), masterData.getStatus()));
        json.put("pictures", partCraftMasterPictureService.getPartCraftPictureMainDataList(masterData.getRandomCode(), masterData.getStatus()));
        return Result.ok(json);
    }


    @RequestMapping(value = "/getDesignPartIsUsed", method = RequestMethod.GET)
    @ApiOperation(value = "getDesignPartIsUsed", notes = "查询设计部件是否用到")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<Boolean> getDesignPartIsUsed(@RequestParam(name = "partCraftRandomCode", required = true) String partCraftRandomCode,
                                               @RequestParam(name = "designPartCode", required = false) String designPartCode,
                                               @RequestParam(name = "positionCode", required = false) String positionCode
    ) {
        Long randomCode = Long.parseLong(partCraftRandomCode);
        List<String> designCodes = new ArrayList<>();
        if (StringUtils.isNotBlank(designPartCode)) {
            designCodes.add(designPartCode);
        }
        List<String> positionCodes = new ArrayList<>();
        if (StringUtils.isNotBlank(positionCode)) {
            positionCodes.add(positionCode);
        }
        boolean ret = partCraftMasterDataService.isDelDesignPartUsed(randomCode, designCodes, positionCodes);
        return Result.ok(ret);
    }


    @RequestMapping(value = "/saveDraftPartCraftInf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "/saveDraftPartCraftInf", notes = "保存部件工艺草稿", httpMethod = "POST", response = Result.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    @Authentication(auth = Authentication.AuthType.EDIT)
    public Result saveDraftPartCraftInf(HttpServletRequest request,
                                        @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false)
                                                UserContext currentUser) throws Exception {

        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        JSONObject json = JSONObject.parseObject(data);
        //部件词库主数据
        PartCraftMasterBasicVo basicVo = json.getObject("masterBasicData", PartCraftMasterBasicVo.class);
        //操作类型
        notNull(json.getInteger("handleType")); //不能为空
        notNull(basicVo); //部件工艺数据不能为空
        int handleType = json.getInteger("handleType");
        if (handleType != BusinessConstants.Status.DRAFT_STATUS) {
            return Result.fail(MessageConstant.HANDLER_TYPE_ERROR);
        }

        if (basicVo.getRandomCode() != null) {
            List<String> designCodes = basicVo.getCraftDesignParts().stream()
                    .filter(x -> BusinessConstants.Status.INVALID_STATUS.equals(x.getStatus()))
                    .map(PartCraftDesignPartsVo::getDesignCode)
                    .collect(Collectors.toList());
            List<String> positionCodes = new ArrayList<>();
            if (basicVo.getCraftPositions() != null) {
                positionCodes = basicVo.getCraftPositions().stream()
                        .filter(x -> BusinessConstants.Status.INVALID_STATUS.equals(x.getStatus()))
                        .map(PartCraftPositionVo::getPartPositionCode)
                        .collect(Collectors.toList());
            }
            if (ObjectUtils.isNotEmptyList(designCodes) || ObjectUtils.isNotEmptyList(positionCodes)) {
                if (partCraftMasterDataService.isDelDesignPartUsed(basicVo.getRandomCode(), designCodes, positionCodes)) {
                    return Result.build(0, "设计部件被智库款引用，不能删除");
                }
            }
        }


        basicVo.setCreateUser(currentUser.getUserName());
        basicVo.setCreateTime(new Date());
        basicVo.setUpdateTime(new Date());
        basicVo.setUpdateUser(currentUser.getUserName());
        basicVo.setReleaseTime(null);
        basicVo.setReleaseUser(null);
        Result result = getCheckParment(basicVo);
        if (result.getCode() != 1) return result;
        PartCraftMasterData masterData = partCraftMasterDataService.verifyPartCraftInf(basicVo);
        if (masterData == null) {
            return Result.build(MessageConstant.MissingServletRequestParameter, MessageConstant.STATUS_TEXT_INTERNAL_ERROR);
        }
        HashMap params = new HashMap();
        params.put("randomCode", masterData.getRandomCode());
        params.put("status", masterData.getStatus());
        PartCraftMasterBasicVo basicVos = partCraftMasterDataService.searchPartCraftInfoRandomCode(params);
        JSONObject jsons = new JSONObject();
        jsons.put("partCraftMaster", basicVos);
        jsons.put("partCraftDetails", partCraftDetailService.getPartCraftDetailMainList(masterData.getRandomCode(), masterData.getStatus()));
        jsons.put("craftDesignParts", partCraftDesignPartsService.getDesignPartsMainList(masterData.getRandomCode(), masterData.getStatus()));
        jsons.put("craftPositions", positionService.getPartCraftPositionMainList(masterData.getRandomCode(), masterData.getStatus()));
        jsons.put("partCraftRules", partCraftRuleService.getRulesList(masterData.getRandomCode(), masterData.getStatus()));
        jsons.put("pictures", partCraftMasterPictureService.getPartCraftPictureMainDataList(masterData.getRandomCode(), masterData.getStatus()));
        return Result.ok(jsons);
    }

    @RequestMapping(value = "/verifyPartCraftInf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "/verifyPartCraftInf", notes = "发布部件工艺", httpMethod = "POST", response = Result.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    @Authentication(auth = Authentication.AuthType.EDIT)
    public Result verifyPartCraftInf(HttpServletRequest request,
                                     @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        JSONObject json = JSONObject.parseObject(data);
        //部件词库主数据
        PartCraftMasterBasicVo basicVo = json.getObject("masterBasicData", PartCraftMasterBasicVo.class);
        //操作类型
        notNull(json.getInteger("handleType")); //不能为空
        notNull(basicVo); //部件工艺数据不能为空
        int handleType = json.getInteger("handleType");
        if (handleType != BusinessConstants.Status.PUBLISHED_STATUS) {
            return Result.fail(MessageConstant.HANDLER_TYPE_ERROR);
        }
        basicVo.setCreateUser(currentUser.getUserName());
        basicVo.setReleaseTime(new Date());
        basicVo.setReleaseUser(currentUser.getUserName());
        Result result = getCheckParment(basicVo);
        if (result.getCode() != 1) return result;
        PartCraftMasterData masterData = partCraftMasterDataService.verifyPartCraftInf(basicVo);
        if (masterData == null) {
            return Result.build(MessageConstant.MissingServletRequestParameter, MessageConstant.STATUS_TEXT_INTERNAL_ERROR);
        } else {
            //检测是否有关联的定制订单
            taskExecutor.execute(() -> {
                List<PICustomOrder> piCustomOrders = ipiCustomOrderService.getCustomOrderInvalidByRandomCode(masterData.getRandomCode());
                for(PICustomOrder customOrder: piCustomOrders) {
                    customStyleCraftCourseService.createCustomStyleCratCouresData(customOrder);
                }

            });
        }
        HashMap params = new HashMap();
        params.put("randomCode", masterData.getRandomCode());
        params.put("status", masterData.getStatus());
        PartCraftMasterBasicVo basicVos = partCraftMasterDataService.searchPartCraftInfoRandomCode(params);
        JSONObject jsons = new JSONObject();
        jsons.put("partCraftMaster", basicVos);
        jsons.put("partCraftDetails", partCraftDetailService.getPartCraftDetailMainList(masterData.getRandomCode(), masterData.getStatus()));
        jsons.put("craftDesignParts", partCraftDesignPartsService.getDesignPartsMainList(masterData.getRandomCode(), masterData.getStatus()));
        jsons.put("craftPositions", positionService.getPartCraftPositionMainList(masterData.getRandomCode(), masterData.getStatus()));
        jsons.put("partCraftRules", partCraftRuleService.getRulesList(masterData.getRandomCode(), masterData.getStatus()));
        jsons.put("pictures", partCraftMasterPictureService.getPartCraftPictureMainDataList(masterData.getRandomCode(), masterData.getStatus()));
        return Result.ok(jsons);
    }

    @RequestMapping(value = "/invalidPartCraft", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "/invalidPartCraft", notes = "删除部件工艺", httpMethod = "POST", response = Result.class)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", dataType = "MessageParam", name = "data", value = "信息参数", required = true)})
    @Authentication(auth = Authentication.AuthType.EDIT)
    public Result invalidPartCraft(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        JSONObject json = JSONObject.parseObject(data);
        //部件词库主数据
        PartCraftMasterBasicVo basicVo = json.getObject("masterBasicData", PartCraftMasterBasicVo.class);
        //操作类型
        notNull(json.getInteger("handleType")); //不能为空
        notNull(basicVo); //部件工艺数据不能为空
        isFalse(partCraftMasterDataService.isThinkStyleUsed(basicVo.getRandomCode()), "设计部件已被智库款使用，不允许删除");
        int handleType = json.getInteger("handleType");
        if (handleType != BusinessConstants.Status.INVALID_STATUS)
            return Result.fail(MessageConstant.HANDLER_TYPE_ERROR);
        notNull(basicVo.getRandomCode());
        return Result.ok(partCraftMasterDataService.invalidPartCraftData(basicVo));
    }

    @RequestMapping(value = "/getPartCraftMasterData", method = RequestMethod.GET)
    @ApiOperation(value = "/getPartCraftMasterData", notes = "根据randomCode获取部件工艺主数据信息")
    @Authentication(auth = Authentication.AuthType.QUERY)
    public Result getPartCraftMasterData(@RequestParam(name = "randomCode", required = true) String randomCode) {
        return Result.ok(partCraftMasterDataService.getPartCraftMasterData(Long.valueOf(randomCode)));
    }

    @RequestMapping(value = "/getPartCraftDetailInfo", method = RequestMethod.GET)
    @ApiOperation(value = "/getPartCraftDetailInfo", notes = "根据编工艺主数据code以及状态查询部件工艺明细信息", response = Result.class)
    @Authentication(auth = Authentication.AuthType.QUERY)
    public Result getPartCraftDetailInfo(@RequestParam(name = "randomCode", required = true) String randomCode,
                                         @RequestParam(name = "status", required = true) Integer status) {
        JSONObject json = new JSONObject();
        json.put("partCraftDetails", partCraftDetailService.getPartCraftDetailMainList(Long.valueOf(randomCode), status));
        json.put("craftDesignParts", partCraftDesignPartsService.getDesignPartsMainList(Long.valueOf(randomCode), status));
        json.put("craftPositions", positionService.getPartCraftPositionMainList(Long.valueOf(randomCode), status));
        json.put("partCraftRules", partCraftRuleService.getRulesList(Long.valueOf(randomCode), status));
        json.put("pictures", partCraftMasterPictureService.getPartCraftPictureMainDataList(Long.valueOf(randomCode), status));
        return Result.ok(json);
    }

    @RequestMapping(value = "/getPartCraftCodeNameAll", method = RequestMethod.GET)
    @ApiOperation(value = "getPartCraftCodeNameAll", httpMethod = "GET", notes = "获取所有部件工艺编码")
    @Authentication(auth = Authentication.AuthType.QUERY)
    public Result getPartCraftCodeNameAll() {
        JSONObject json = new JSONObject();
        json.put("partCraftNames", partCraftMasterDataService.getPartCraftCodeNameAll());
        return Result.ok(json);
    }

    /**
     * 检查基础数据
     *
     * @param basicVo
     * @return
     */
    private boolean checkBasicData(PartCraftMasterBasicVo basicVo) {
        boolean result = true;
        if (ObjectUtils.isEmpty(basicVo) ||
                StringUtils.isEmpty(basicVo.getCraftCategoryCode()) ||
                StringUtils.isEmpty(basicVo.getCraftPartCode()) ||
                StringUtils.isEmpty(basicVo.getPartCraftMainName())) {
            result = false;
        }
        return result;
    }

    private Result getCheckParment(PartCraftMasterBasicVo basicVo) {
        JSONObject jsons = null;
        List<String> designPartCodes = new ArrayList<>();
        List<String> positionCodes = new ArrayList<>();
        List<PartCraftPositionVo> checkPositionVoList = new ArrayList<>();
        List<PartCraftDesignPartsVo> checkDesignPartList = new ArrayList<>();

        if (ObjectUtils.isNotEmptyList(basicVo.getCraftDesignParts())) {
            basicVo.getCraftDesignParts().forEach(part -> {
                if (!part.getStatus().equals(BusinessConstants.Status.INVALID_STATUS)) {

                    checkDesignPartList.add(part);

                }
                if (!part.getStatus().equals(BusinessConstants.Status.INVALID_STATUS) &&
                        (part.getRandomCode() == null && part.getId() == null)) {
                    designPartCodes.add(part.getDesignCode());

                }
            });
        }
        if (ObjectUtils.isNotEmptyList(basicVo.getCraftPositions())) {
            basicVo.getCraftPositions().forEach(position -> {
                if (!position.getStatus().equals(BusinessConstants.Status.INVALID_STATUS)) {

                    checkPositionVoList.add(position);
                }
                if (!position.getStatus().equals(BusinessConstants.Status.INVALID_STATUS) &&
                        (position.getRandomCode() == null && position.getId() == null)) {
                    positionCodes.add(position.getPartPositionCode());

                }
            });
        }
        if (basicVo.getPartType().equalsIgnoreCase(PartCraftPartType.conventionalPartCraft) ||
                basicVo.getPartType().equalsIgnoreCase(PartCraftPartType.hiddenPartCraft)) {
            if (ObjectUtils.isNotEmptyList(checkDesignPartList)) {
                if (checkDesignPartList.size() > 1) {
                    jsons = partCraftMasterDataService.checkPartCraftDesignPartParments(checkDesignPartList);
                    if (jsons.getBoolean("cpl")) {
                        return Result.build(BusinessConstants.CommonConstant.ZERO, jsons.getString("msg"));
                    }
                }
                //判断当前工艺设计部件是否与其它部件工艺设计部件存在相同编码
                if (designPartCodes.size() > 0) {
                    jsons = partCraftMasterDataService.checkPartCraftDesignPartParments(basicVo.getPartType(), designPartCodes);
                    if (jsons != null) {
                        if (jsons.getBoolean("cpl")) {
                            return Result.build(BusinessConstants.CommonConstant.ZERO, jsons.getString("msg"));
                        }
                    }
                }
            }
            if (ObjectUtils.isNotEmptyList(checkPositionVoList)) {
                if (checkPositionVoList.size() > 1) {
                    jsons = partCraftMasterDataService.checkPartCraftPositionParments(checkPositionVoList);
                    if (jsons.getBoolean("cpl")) {
                        return Result.build(BusinessConstants.CommonConstant.ZERO, jsons.getString("msg"));
                    }
                }
            }
        } else {
            if (ObjectUtils.isEmptyList(basicVo.getCraftDesignParts())) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "设计部件数据为空");
            } else if (ObjectUtils.isEmptyList(basicVo.getCraftPositions())) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "部件位置数据为空");
            } else if (ObjectUtils.isNotEmptyList(basicVo.getCraftPositions()) && ObjectUtils.isNotEmptyList(basicVo.getCraftDesignParts())) {

                if (checkDesignPartList.size() < 1) {
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "设计部件数据为空");
                }
                if (checkPositionVoList.size() < 1) {
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "部件位置数据为空");
                }
                if (checkDesignPartList.size() > 1) {
                    jsons = partCraftMasterDataService.checkPartCraftDesignPartParments(checkDesignPartList);
                }
                if (positionCodes.size() > 1) {
                    jsons = partCraftMasterDataService.checkPartCraftPositionParments(checkPositionVoList);
                }

                //判断当前工艺设计部件是否与其它部件工艺设计部件存在相同编码
                if (designPartCodes.size() > 0) {
                    jsons = partCraftMasterDataService.checkPartCraftDesignPartParments(basicVo.getPartType(), designPartCodes);
                    if (jsons != null) {
                        if (jsons.getBoolean("cpl")) {
                            return Result.build(BusinessConstants.CommonConstant.ZERO, jsons.getString("msg"));
                        }
                    }
                }
            }
        }
        if (ObjectUtils.isNotEmpt(basicVo.getPartCraftRules())) {
            for (PartCraftRuleVo partCraftRule : basicVo.getPartCraftRules()) {
                if (partCraftRule.getProcessType() == null) {
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "规则类型不能为空");
                }
            }
            Boolean bol = partCraftMasterDataService.checkPartCraftRulePaements(basicVo.getPartCraftRules());
            if (bol) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "工艺规则-替换或者减少类型来源工序字段不能为空");
            }
        }
        if (ObjectUtils.isNotEmptyList(basicVo.getPartCraftDetails())) {
            Boolean bol = partCraftMasterDataService.checkPartCraftDetailPaements(basicVo.getPartCraftDetails());
            if (bol) {
                return Result.build(BusinessConstants.CommonConstant.ZERO, "当前部件工艺工序存在相同工序");
            }
        }
        return Result.ok();
    }
}
