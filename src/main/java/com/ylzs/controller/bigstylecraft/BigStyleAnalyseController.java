package com.ylzs.controller.bigstylecraft;

import cn.hutool.core.collection.CollUtil;
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
import com.ylzs.controller.sewingcraft.SewingCraftWarehouseController;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraft;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.entity.craftmainframe.FlowNumConfig;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.entity.craftstd.CraftPart;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.entity.plm.BigStyleMasterData;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.staticdata.PatternSymmetry;
import com.ylzs.entity.system.User;
import com.ylzs.entity.system.WebConfig;
import com.ylzs.service.bigstylecraft.BigStyleAnalyseMasterService;
import com.ylzs.service.bigstylecraft.BigStyleOperationLogService;
import com.ylzs.service.bigstylecraft.StyleSewingCraftWarehouseService;
import com.ylzs.service.bigticketno.BigOrderMasterService;
import com.ylzs.service.craftmainframe.ICraftMainFrameRouteService;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.service.craftmainframe.IProductionPartService;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.craftstd.ICraftPartService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.factory.IProductionGroupService;
import com.ylzs.service.partCraft.IPartCraftMasterDataService;
import com.ylzs.service.partCraft.IPartCraftMasterPictureService;
import com.ylzs.service.plm.IBigStyleMasterDataService;
import com.ylzs.service.sewingcraft.SewingCraftActionService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import com.ylzs.service.staticdata.PatternSymmetryService;
import com.ylzs.service.system.IUserService;
import com.ylzs.service.system.IWebConfigService;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.bigstylereport.CraftVO;
import com.ylzs.vo.partCraft.PartCraftDetailVo;
import com.ylzs.vo.partCraft.PartCraftMasterBasicVo;
import com.ylzs.vo.partCraft.PartCraftMasterPictureVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xuwei
 * @create 2020-03-14 16:55
 * 款式工艺--------大货款
 */
@RestController
@RequestMapping("/bigStyleAnalyse")
@Api(tags = "大货款式工艺")
public class BigStyleAnalyseController {

    @Resource
    private IPartCraftMasterDataService partCraftMasterDataService;

    @Resource
    private IDictionaryService dictionaryService;

    @Resource
    private ICraftCategoryService craftCategoryService;

    @Resource
    private ICraftMainFrameService mainFrameService;

    @Resource
    private IUserService userService;

    @Resource
    private BigStyleAnalyseMasterService bigStyleAnalyseMasterService;

    @Resource
    private IBigStyleMasterDataService bigStyleMasterDataService;

    @Resource
    private SewingCraftActionService sewingCraftActionService;

    @Resource
    private ICraftPartService craftPartService;

    @Resource
    private SewingCraftWarehouseService sewingCraftWarehouseService;

    @Resource
    private PatternSymmetryService patternSymmetryService;

    @Resource
    private IPartCraftMasterPictureService partCraftMasterPictureService;

    @Resource
    private StyleSewingCraftWarehouseService styleSewingCraftWarehouseService;
    @Resource
    private BigOrderMasterService bigOrderMasterService;

    @Resource
    private BigStyleOperationLogService bigStyleOperationLogService;

    @Resource
    private IWebConfigService webConfigService;

    @Resource
    private ICraftMainFrameRouteService craftMainFrameRouteService;

    @Resource
    private IProductionPartService productionPartService;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private IProductionGroupService productionGroupService;


    private static final Logger LOGGER = LoggerFactory.getLogger(BigStyleAnalyseController.class);

    /**
     * 工艺品类和服装品类的条件选择
     */
    @ApiOperation(value = "getDropDownData", notes = "新工艺品类和服装品类的条件选择")
    @RequestMapping(value = "/getDropDownData", method = RequestMethod.GET)
    public Result<JSONObject> getDropDownData() {
        JSONObject obj = new JSONObject();
        //服装大类
        List<Dictionary> dictionaryList = dictionaryService.getDictionaryByTypeCode(BusinessConstants.DictionaryType.CLOTHES_CATEGORY);
        obj.put("dictionaryList", dictionaryList);

        //子品牌
        List<Dictionary> subBrandList = dictionaryService.getDictionaryByTypeCode(BusinessConstants.DictionaryType.SUB_BRAND);
        obj.put("subBrandList", subBrandList);

        //包装方式
        List<Dictionary> packingList = dictionaryService.getDictionaryByTypeCode(BusinessConstants.DictionaryType.PACKING_METHOD);
        obj.put("packingList", packingList);

        //工艺品类
        List<CraftCategory> craftCategoryList = craftCategoryService.getCraftCategoryClothesCategory();
        obj.put("craftCategoryList", craftCategoryList);
        //工艺主框架
        List<CraftMainFrame> craftMainFrameList = mainFrameService.getByCondition(null, null, null);
        obj.put("craftMainFrameList", craftMainFrameList);


        //用户
        List<User> userList = userService.getAllUser();
        obj.put("userList", userList);
        //结构部件--工艺部件
        List<CraftPart> craftPartList = craftPartService.getAllValidCraftPart();
        obj.put("craftPartList", craftPartList);

        //条格类型
        List<PatternSymmetry> symmetryList = patternSymmetryService.getAllPatternSymmetrys();
        obj.put("symmetryList", symmetryList);
        return Result.ok(MessageConstant.SUCCESS, obj);
    }

    @RequestMapping(value = "/getCraftNo", method = RequestMethod.GET)
    @ApiOperation(value = "getCraftNo", notes = "根据工序编码和工艺主框架查询工序流", httpMethod = "GET", response = Result.class)
    public Result<List<JSONObject>> getCraftNo(@RequestParam(name = "mainFrameCode", required = true) String mainFrameCode,
                                               @RequestParam(name = "craftCodeList", required = true) String[] craftCodeList) {
        List<String> codeList = Arrays.asList(craftCodeList);
        Map<String, SewingCraftWarehouse> map = sewingCraftWarehouseService.getCraftFlowNumByCraftCodeAndMainFrameCode(codeList, mainFrameCode);
        List<JSONObject> result = new ArrayList<>();
        for (String code : codeList) {
            JSONObject obj = new JSONObject();
            obj.put("craftCode", code);
            if (map.containsKey(code)) {
                obj.put("craftNo", map.get(code).getCraftFlowNum());
            }
            result.add(obj);
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/searchFromBigStyleAnalyse", method = RequestMethod.GET)
    @ApiOperation(value = "searchFromBigStyleAnalyse", notes = "根据款式工艺导入", httpMethod = "GET", response = Result.class)
    public Result<List<BigStyleAnalyseMaster>> searchFromBigStyleAnalyse(@RequestParam(defaultValue = "1") Integer page,
                                                                         @RequestParam(defaultValue = "30") Integer rows,
                                                                         @RequestParam(name = "param", required = false) String param,
                                                                         @RequestParam(name = "bigStyleAnalyseCode", required = false) String bigStyleAnalyseCode,
                                                                         @RequestParam(name = "styleDesc", required = false) String styleDesc,
                                                                         @RequestParam(name = "subBrand", required = false) String subBrand,
                                                                         @RequestParam(name = "description", required = false) String description,
                                                                         @RequestParam(name = "craftCode", required = false) String craftCode,
                                                                         @RequestParam(name = "styleName", required = false) String styleName,
                                                                         @RequestParam(name = "productionCategory", required = false) String productionCategory) {

        PageHelper.startPage(page, rows);
        Map<String, Object> map = new HashMap<>();
        map.put("param", param);
        if (StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            bigStyleAnalyseCode = StringUtils.replaceBlank(bigStyleAnalyseCode);
            map.put("bigStyleAnalyseCode", bigStyleAnalyseCode);
        }
        if (StringUtils.isNotEmpty(styleDesc)) {
            styleDesc = StringUtils.replaceBlank(styleDesc);
            map.put("styleDesc", styleDesc);
        }
        if (StringUtils.isNotEmpty(styleName)) {
            styleName = StringUtils.replaceBlank(styleName);
            map.put("styleName", styleName);
        }
        if (StringUtils.isNotEmpty(subBrand)) {
            subBrand = StringUtils.replaceBlank(subBrand);
            List<DictionaryVo> vos = dictionaryService.getDictoryValueList("subBrand", subBrand);
            if (null != vos && vos.size() > 0) {
                map.put("subBrand", vos.get(0).getDicValue());
            } else {
                map.put("subBrand", subBrand);
            }
        }
        if (StringUtils.isNotEmpty(craftCode)) {
            craftCode = StringUtils.replaceBlank(craftCode);
            map.put("craftCode", craftCode);
        }
        if (StringUtils.isNotEmpty(description)) {
            description = StringUtils.replaceBlank(description);
            map.put("description", description);
        }
        //查询发布状态
        map.put("status", BusinessConstants.Status.PUBLISHED_STATUS);

        List<BigStyleAnalyseMaster> data = bigStyleAnalyseMasterService.searchFromBigStyleAnalyse(map);
        PageInfo<BigStyleAnalyseMaster> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());
    }

    @RequestMapping(value = "/searchFromPartCraftInfo", method = RequestMethod.GET)
    @ApiOperation(value = "searchFromPartCraftInfo", notes = "从部件工艺导入", httpMethod = "GET", response = Result.class)
    public Result<List<BigStyleAnalysePartCraft>> searchFromPartCraftInfo(@RequestParam(defaultValue = "1") Integer page,
                                                                          @RequestParam(defaultValue = "30") Integer rows,
                                                                          @RequestParam(name = "partCraftMainName", required = false) String partCraftMainName,
                                                                          @RequestParam(name = "partCraftMainCode", required = false) String partCraftMainCode,
                                                                          @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                                                          @RequestParam(name = "mainFrameCode", required = true) String mainFrameCode) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("mainFrameCode", mainFrameCode);
        if (StringUtils.isNotEmpty(partCraftMainCode)) {
            partCraftMainCode = StringUtils.replaceBlank(partCraftMainCode);
            params.put("partCraftMainCode", partCraftMainCode);
        }
        if (StringUtils.isNotEmpty(partCraftMainName)) {
            partCraftMainName = StringUtils.replaceBlank(partCraftMainName);
            params.put("partCraftMainName", partCraftMainName);
        }
        if (StringUtils.isNotEmpty(craftCategoryCode)) {
            craftCategoryCode = StringUtils.replaceBlank(craftCategoryCode);
            params.put("craftCategoryCode", craftCategoryCode);
        }
        //因为有子查询，这个分页插件不好用
        PageHelper.startPage(page, rows);
        List<PartCraftMasterBasicVo> onlyPartCraftInfolist = partCraftMasterDataService.searchOnlyPartCraftInfo(params);
        PageInfo<PartCraftMasterBasicVo> pageInfo = new PageInfo<>(onlyPartCraftInfolist);
        List<String> codeList = new ArrayList<>();
        if (null != onlyPartCraftInfolist && onlyPartCraftInfolist.size() > 0) {
            for (PartCraftMasterBasicVo vo : onlyPartCraftInfolist) {
                codeList.add(vo.getPartCraftMainCode());
            }
        }
        params.put("codeList", codeList);
        List<PartCraftMasterBasicVo> list = partCraftMasterDataService.searchPartCraftAndDetailInfo(params);
        //通过车缝工序编码查询工序下面的动作代码
        //getSewingCraftAction(list);
        List<BigStyleAnalysePartCraft> data = partCraftChangeToBigStyleAnalyse(list);

        return Result.ok(data, pageInfo.getTotal());
        /*int currentPage = page;
        int pageSize = rows;
        long totalCount = data.size();
        //总页数
        long pageCount = 0;
        if (totalCount % pageSize > 0) {
            pageCount = totalCount / pageSize + 1;
        } else {
            pageCount = totalCount / pageSize;
        }
        if (currentPage > pageCount) {
            return Result.ok(Collections.emptyList(), totalCount);
        } else if (currentPage == pageCount) {
            return Result.ok(data.subList((currentPage - 1) * pageSize, (int) totalCount), totalCount);
        } else {
            return Result.ok(data.subList((currentPage - 1) * pageSize, (int) currentPage * pageSize), totalCount);
        }*/
    }

    private List<BigStyleAnalysePartCraft> partCraftChangeToBigStyleAnalyse(List<PartCraftMasterBasicVo> partCraftMasterBasicVoList) {
        if (null == partCraftMasterBasicVoList || partCraftMasterBasicVoList.size() == 0) {
            return Collections.emptyList();
        }
        List<BigStyleAnalysePartCraft> result = new ArrayList<>();
        Map<String, CraftMainFrame> mainFrameMap = mainFrameService.getAllMainFrameToMap();
        for (PartCraftMasterBasicVo vo : partCraftMasterBasicVoList) {
            BigStyleAnalysePartCraft big = new BigStyleAnalysePartCraft();
            big.setPartCode(vo.getCraftPartCode());
            big.setPartName(vo.getCraftPartName());
            big.setPartCraftMainCode(vo.getPartCraftMainCode());
            big.setPartCraftMainName(vo.getPartCraftMainName());
            big.setCreateUser(vo.getCreateUser());
            big.setCreateTime(vo.getCreateTime());
            big.setRemark(vo.getRemark());
            List<BigStyleAnalysePartCraftDetail> details = new ArrayList<>();
            if (vo.getPartCraftDetails() != null && vo.getPartCraftDetails().size() > 0) {
                for (PartCraftDetailVo detailVo : vo.getPartCraftDetails()) {
                    BigStyleAnalysePartCraftDetail bigDetail = new BigStyleAnalysePartCraftDetail();
                    bigDetail.setCraftCode(detailVo.getCraftCode());
                    bigDetail.setMachineCode(detailVo.getMachineCode());
                    bigDetail.setMachineName(detailVo.getMachineName());
                    bigDetail.setCraftNo(detailVo.getCraftFlowNum());
                    bigDetail.setStandardPrice(detailVo.getStandardPrice());
                    bigDetail.setStandardTime(detailVo.getStandardTime());
                    //工序等级
                    bigDetail.setCraftGradeCode(detailVo.getCraftGradeCode());
                    bigDetail.setCraftName(detailVo.getCraftName());
                    bigDetail.setMotionList(detailVo.getMotionList());
                    bigDetail.setCraftRemark(detailVo.getCraftRemark());
                    if (mainFrameMap.containsKey(detailVo.getCraftMainFrameCode())) {
                        bigDetail.setMainFrameName(mainFrameMap.get(detailVo.getCraftMainFrameCode()).getMainFrameName());
                    }
                    bigDetail.setMainFrameCode(detailVo.getCraftMainFrameCode());
                    details.add(bigDetail);
                }
            }
            big.setPartCraftDetailList(details);
            List<PartCraftMasterPictureVo> pics = partCraftMasterPictureService.getPictureByPartCraftMainRandomCode(vo.getRandomCode());
            big.setPictures(pics);
            result.add(big);
        }
        return result;
    }

    /**
     * 从车缝工序中导入
     */
    @RequestMapping(value = "/searchFromSewingCraftInfo", method = RequestMethod.GET)
    @ApiOperation(value = "searchFromSewingCraftInfo", notes = "从工序库导入", httpMethod = "GET", response = Result.class)
    public Result<List<BigStyleAnalysePartCraftDetail>> searchFromSewingCraftInfo(@RequestParam(defaultValue = "1") Integer page,
                                                                                  @RequestParam(defaultValue = "30") Integer rows,
                                                                                  @RequestParam(name = "categoryCode", required = true) String categoryCode,
                                                                                  @RequestParam(name = "craftName", required = false) String craftName,
                                                                                  @RequestParam(name = "craftCode", required = false) String craftCode,
                                                                                  @RequestParam(name = "description", required = false) String description,
                                                                                  @RequestParam(name = "craftPartCode", required = false) String craftPartCode,
                                                                                  @RequestParam(name = "mainFrameCode", required = false) String mainFrameCode) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(craftName)) {
            craftName = StringUtils.replaceBlank(craftName);
            params.put("craftName", craftName);
        }
        if (StringUtils.isNotEmpty(mainFrameCode)) {
            mainFrameCode = StringUtils.replaceBlank(mainFrameCode);
            params.put("mainFrameCode", mainFrameCode);
        }
        if (StringUtils.isNotEmpty(craftCode)) {
            craftCode = StringUtils.replaceBlank(craftCode);
            params.put("craftCode", craftCode);
        }
        if (StringUtils.isNotEmpty(craftPartCode)) {
            craftPartCode = StringUtils.replaceBlank(craftPartCode);
            params.put("craftPartCode", craftPartCode);
        }
        if (StringUtils.isNotEmpty(categoryCode)) {
            categoryCode = StringUtils.replaceBlank(categoryCode);
            params.put("categoryCode", categoryCode);
        }
        if (StringUtils.isNotEmpty(description)) {
            description = StringUtils.replaceBlank(description);
            params.put("description", description);
        }
        PageHelper.startPage(page, rows);
        List<SewingCraftWarehouse> sewingCraftWarehouseList = sewingCraftWarehouseService.getDataByMainFramePartCode(params);
        PageInfo<SewingCraftWarehouse> pageInfo = new PageInfo<>(sewingCraftWarehouseList);
        List<BigStyleAnalysePartCraftDetail> list = sewingChangeToBigStyleAnalyse(sewingCraftWarehouseList);
        return Result.ok(list, pageInfo.getTotal());
    }

    private List<BigStyleAnalysePartCraftDetail> sewingChangeToBigStyleAnalyse(List<SewingCraftWarehouse> sewingCraftWarehouseList) {
        if (null == sewingCraftWarehouseList || sewingCraftWarehouseList.size() == 0) {
            return Collections.emptyList();
        }
        List<BigStyleAnalysePartCraftDetail> result = new ArrayList<>();
        for (SewingCraftWarehouse vo : sewingCraftWarehouseList) {
            BigStyleAnalysePartCraftDetail detailVo = new BigStyleAnalysePartCraftDetail();
            detailVo.setCraftCode(vo.getCraftCode());//工序编码
            if (vo.getCraftFlowNum() != null) {
                detailVo.setCraftNo(vo.getCraftFlowNum().toString());//工序流
            }
            detailVo.setRandomCode(vo.getRandomCode());
            detailVo.setMachineCode(vo.getMachineCode());
            detailVo.setMachineName(vo.getMachineName());
            detailVo.setCraftRemark(vo.getDescription());
            detailVo.setCraftName(vo.getCraftName());
            detailVo.setStandardPrice(vo.getStandardPrice());
            detailVo.setStandardTime(vo.getStandardTime());
            detailVo.setCraftGradeCode(vo.getCraftGradeCode());
            detailVo.setMotionList(vo.getMotionsList());
            detailVo.setCraftCategoryCode(vo.getCraftCategoryCode());
            detailVo.setCraftCategoryName(vo.getCraftCategoryName());
            detailVo.setMainFrameCode(vo.getMainFrameCode());
            detailVo.setMainFrameName(vo.getMainFrameName());
            detailVo.setVedioUrl(vo.getVedioUrl());
            detailVo.setSketchPicUrl(vo.getSketchPicUrl());
            detailVo.setCraftPartName(vo.getCraftPartName());
            result.add(detailVo);
        }
        return result;
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
        LOGGER.info("从部件工艺库导入的参数：" + data);
        JSONObject dataObj = JSONObject.parseObject(data);
        //参数校验
        try {
            Result<JSONObject> paramCheckResult = checkParam(dataObj);
            if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
                LOGGER.error("参数为空，" + JSONObject.toJSONString(paramCheckResult));
                return paramCheckResult;
            }
        } catch (Exception e) {
            Result.ok(MessageConstant.SERVER_FIELD_ERROR, result.put("msg", e.getMessage()));
        }

        //randomCode
        String randomCode = dataObj.getString("randomCode");
        //craftCode
        String bigStyleAnalyseCode = dataObj.getString("bigStyleAnalyseCode");
        String operation = BusinessConstants.Send2Pi.actionType_create;
        String userCode = dataObj.getString("userCode");
        if (StringUtils.isNotEmpty(randomCode) && StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            LOGGER.info("------大货款式分析操作类型是update--------");
            operation = BusinessConstants.Send2Pi.actionType_update;
        }
        LOGGER.error(userCode + "--" + randomCode + "--" + bigStyleAnalyseCode + "--" + operation + "--" + "big_style_analyse_master");
        result = bigStyleAnalyseMasterService.addOrUpdate(dataObj, operation);

        if (result != null && StringUtils.isNotEmpty(result.getString("bigStyleAnalyseCode")) && result.getLong("randomCode") != null) {
            return Result.ok(MessageConstant.SUCCESS, result);
        } else {
            return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
        }
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
        /*String partCrafts = dataObj.getString("partCraftList");
        List<BigStyleAnalysePartCraftDetail> partCraftDetailList = JSONObject.parseArray(partCrafts, BigStyleAnalysePartCraftDetail.class);
        if (null == partCraftDetailList || partCraftDetailList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "工序数据为空");
        }*/
        //款式编码
        String styleName = dataObj.getString("styleName");
        if (StringUtils.isEmpty(styleName)) {
            return Result.build(MessageConstant.PARAM_NULL, "款式名称为空");
        }
        //款式编码
        String ctStyleCode = dataObj.getString("ctStyleCode");
        if (StringUtils.isEmpty(ctStyleCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "款式编码为空");
        }

        //客户--子品类
        String subBrand = dataObj.getString("subBrand");
        if (StringUtils.isEmpty(subBrand)) {
            return Result.build(MessageConstant.PARAM_NULL, "客户为空");
        }
        //服装品类
        String clothesCategoryCode = dataObj.getString("clothesCategoryCode");
        if (StringUtils.isEmpty(clothesCategoryCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "服装品类名称为空");
        }
        if (StringUtils.isEmpty(dataObj.getString("forSalesTime"))) {
            dataObj.put("forSalesTime", null);
        }
        //面料分值
        String fabricFraction = dataObj.getString("fabricFraction");
        if (StringUtils.isEmpty(fabricFraction)) {
            return Result.build(MessageConstant.PARAM_NULL, "面料分值为空");
        }
        //工艺主框架编码
        String mainFrameCode = dataObj.getString("mainFrameCode");
        if (StringUtils.isEmpty(mainFrameCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "工艺主框架编码为空");
        }

        String styleDesc = dataObj.getString("styleDesc");
        if (StringUtils.isNotBlank(styleDesc)) {
            if (styleDesc.length() > 500) {
                return Result.build(MessageConstant.PARAM_NULL, "款式描述太长");
            }
        }


        return Result.build(MessageConstant.SUCCESS, "参数校验OK");
    }

    /**
     * 通过车缝工序编码查询工序下面的动作代码
     */
    private void getSewingCraftAction(List<PartCraftMasterBasicVo> list) {
        if (null == list || list.size() == 0) {
            return;
        }
        //层级关系是，部件工艺--》部件工艺详情--》车缝工序--》动作代码
        list.stream().parallel().forEach(master -> {
            List<PartCraftDetailVo> partCraftDetails = master.getPartCraftDetails();
            if (null != partCraftDetails && partCraftDetails.size() > 0) {
                for (PartCraftDetailVo detailVo : partCraftDetails) {
                    List<SewingCraftAction> motionList = sewingCraftActionService.getDataBySewingCraftCode(detailVo.getCraftCode());
                    detailVo.setMotionList(motionList);
                }
            }
        });
    }

    @RequestMapping(value = "/searchBigStyleInfo", method = RequestMethod.GET)
    @ApiOperation(value = "searchBigStyleInfo", notes = "根据款号查询大货款", httpMethod = "GET")
    public Result<List<BigStyleMasterData>> searchBigStyleInfo(@RequestParam(defaultValue = "1") Integer page,
                                                               @RequestParam(defaultValue = "30") Integer rows,
                                                               @RequestParam(name = "ctStyleCode", required = false) String ctStyleCode) {
        HashMap<String, Object> params = new HashMap();
        if (StringUtils.isNotEmpty(ctStyleCode)) {
            params.put("ctStyleCode", ctStyleCode.toUpperCase());
        }
        PageHelper.startPage(page, rows);
        List<BigStyleMasterData> list = bigStyleMasterDataService.getAllDataForPage(params);
        PageInfo<BigStyleMasterData> pageInfo = new PageInfo<BigStyleMasterData>(list);
        return Result.ok(list, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询所有的款式工艺")
    public Result<List<BigStyleAnalyseMaster>> getAll(@RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "30") Integer rows,
                                                      @RequestParam(name = "mainFrameCode", required = false) String mainFrameCode,
                                                      @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                                      @RequestParam(name = "clothesCategoryCode", required = false) String clothesCategoryCode,
                                                      @RequestParam(name = "createUser", required = false) String createUser,
                                                      @RequestParam(name = "releaseUser", required = false) String releaseUser,
                                                      @RequestParam(name = "bigStyleAnalyseCode", required = false) String bigStyleAnalyseCode,
                                                      @RequestParam(name = "styleDesc", required = false) String styleDesc,
                                                      @RequestParam(name = "status", required = false) Long status,
                                                      @RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                                                      @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate,
                                                      @RequestParam(name = "releaseTimeBeginDate", required = false) String releaseTimeBeginDate,
                                                      @RequestParam(name = "releaseTimeEndDate", required = false) String releaseTimeEndDate) {

        LOGGER.info("列表页面查询参数是: page" + page + " rows:" + rows + " mainFrameCode:" + mainFrameCode);
        Map<String, Object> param = new HashMap<>();
        param.put("mainFrameCode", mainFrameCode);
        param.put("craftCategoryCode", craftCategoryCode);
        param.put("clothesCategoryCode", clothesCategoryCode);
        List<User> users = userService.getAllUser();
        List<String> createUserList = new ArrayList<>();
        List<String> releaseUserList = new ArrayList<>();
        for (User user : users) {
            String name = user.getUserName();
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(createUser) && name.indexOf(createUser) != -1) {
                createUserList.add(user.getUserCode());
            }
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(releaseUser) && name.indexOf(releaseUser) != -1) {
                releaseUserList.add(user.getUserCode());
            }
        }
        param.put("createUser", createUserList);
        param.put("releaseUser", releaseUserList);
        if (StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            bigStyleAnalyseCode = StringUtils.replaceBlank(bigStyleAnalyseCode);
        }
        if (StringUtils.isNotEmpty(styleDesc)) {
            styleDesc = StringUtils.replaceBlank(styleDesc);
        }
        param.put("bigStyleAnalyseCode", bigStyleAnalyseCode);
        param.put("styleDesc", styleDesc);
        param.put("status", status);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isNotEmpty(createTimeBeginDate)) {
                Date createTimeBegin = sdf.parse(createTimeBeginDate);
                param.put("createTimeBeginDate", createTimeBegin);
            }
            if (StringUtils.isNotEmpty(createTimeEndDate)) {
                Date createTimeEnd = sdf.parse(createTimeEndDate);
                param.put("createTimeEndDate", createTimeEnd);
            }
            if (StringUtils.isNotEmpty(releaseTimeBeginDate)) {
                Date releaseTimeBegin = sdf.parse(releaseTimeBeginDate);
                param.put("releaseTimeBeginDate", releaseTimeBegin);
            }
            if (StringUtils.isNotEmpty(releaseTimeEndDate)) {
                Date releaseTimeEnd = sdf.parse(releaseTimeEndDate);
                param.put("releaseTimeEndDate", releaseTimeEnd);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        PageHelper.startPage(page, rows);
        List<BigStyleAnalyseMaster> data = bigStyleAnalyseMasterService.getDataByPager(param);
        PageInfo<BigStyleAnalyseMaster> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getBigStyleAnalyseByRandomCode", method = RequestMethod.GET)
    @ApiOperation(value = "getBigStyleAnalyseByRandomCode", notes = "通过randomCode查询单个款式工艺")
    public Result<BigStyleAnalyseMaster> getBigStyleAnalyseByRandomCode(@RequestParam(name = "randomCode", required = true) String randomCode) {
        LOGGER.info("查询的randomCode：" + randomCode);
        if (StringUtils.isEmpty(randomCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        Long ran = Long.parseLong(randomCode);
        BigStyleAnalyseMaster data = bigStyleAnalyseMasterService.searchFromBigStyleAnalyseByRandomCode(ran);
        return Result.ok(MessageConstant.SUCCESS, data);
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "updateStatus", notes = "修改款式状态")
    public Result<JSONObject> updateStatus(@RequestParam(name = "randomCode", required = true) String randomCode,
                                           @RequestParam(name = "status", required = true) Integer status,
                                           @RequestParam(name = "userCode", required = false) String userCode) throws Exception {
        Long randomCodeLong = Long.parseLong(randomCode);
        if (bigStyleAnalyseMasterService.updateStatus(status, randomCodeLong, userCode)) {
            JSONObject obj = new JSONObject();
            obj.put("randomCode", randomCode);
            obj.put("status", status);
            return Result.ok(MessageConstant.SUCCESS, obj);
        }
        return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
    }


    @RequestMapping(value = "/flushCraftNo", method = RequestMethod.GET)
    @ApiOperation(value = "flushCraftNo", notes = "修改工艺主框架后，刷新工序流")
    public Result<Map<String, Integer>> flushCraftNo(@RequestParam(value = "codeList", required = true) String[] codeList,
                                                     @RequestParam(value = "mainFrameCode", required = true) String mainFrameCode) throws Exception {
        LOGGER.info("codeList：" + codeList);
        if (null == codeList || codeList.length == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "codeList参数为空");
        }
        if (StringUtils.isEmpty(mainFrameCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "mainFrameCode参数为空");
        }
        List<String> codes = Arrays.asList(codeList);
        Map<String, Integer> result = new HashMap<>();
        //因为只需要工序流，去掉多余的字段
        Map<String, SewingCraftWarehouse> map = sewingCraftWarehouseService.getCraftFlowNumByCraftCodeAndMainFrameCode(codes, mainFrameCode);
        if (null != map && map.size() > 0) {
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                result.put(key, map.get(key).getCraftFlowNum());
            }
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/getCraftCategoryAndClothesCategory", method = RequestMethod.GET)
    @ApiOperation(value = "getCraftCategoryAndClothesCategory", notes = "查询工艺品类对应服装品类")
    public Result<List<Map<String, String>>> getCraftCategoryAndClothesCategory() throws Exception {
        List<Map<String, String>> result = craftCategoryService.getCraftCategoryAndClothesCategory();
        return Result.ok(result);
    }

    @ApiOperation(value = "exportBigStyleAnalyse", notes = "导出款式工艺数据清单", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportBigStyleAnalyse", method = RequestMethod.GET)
    public Result exportBigStyleAnalyse(@RequestParam(name = "mainFrameCode", required = false) String mainFrameCode,
                                        @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                        @RequestParam(name = "clothesCategoryCode", required = false) String clothesCategoryCode,
                                        @RequestParam(name = "createUser", required = false) String createUser,
                                        @RequestParam(name = "releaseUser", required = false) String releaseUser,
                                        @RequestParam(name = "bigStyleAnalyseCode", required = false) String bigStyleAnalyseCode,
                                        @RequestParam(name = "styleDesc", required = false) String styleDesc,
                                        @RequestParam(name = "status", required = false) Long status,
                                        @RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                                        @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate,
                                        @RequestParam(name = "releaseTimeBeginDate", required = false) String releaseTimeBeginDate,
                                        @RequestParam(name = "releaseTimeEndDate", required = false) String releaseTimeEndDate,
                                        HttpServletResponse response) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("mainFrameCode", mainFrameCode);
        param.put("craftCategoryCode", craftCategoryCode);
        param.put("clothesCategoryCode", clothesCategoryCode);
        param.put("bigStyleAnalyseCode", bigStyleAnalyseCode);
        param.put("styleDesc", styleDesc);
        param.put("status", status);
        List<User> users = userService.getAllUser();
        Map<String, String> userMap = new HashMap<>();
        List<String> createUserList = new ArrayList<>();
        List<String> releaseUserList = new ArrayList<>();
        doneForPage();
        for (User user : users) {
            userMap.put(user.getUserCode(), user.getUserName());
            String name = user.getUserName();
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(createUser) && name.indexOf(createUser) != -1) {
                createUserList.add(user.getUserCode());
            }
            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(releaseUser) && name.indexOf(releaseUser) != -1) {
                releaseUserList.add(user.getUserCode());
            }
        }
        param.put("createUser", createUserList);
        param.put("releaseUser", releaseUserList);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isNotEmpty(createTimeBeginDate)) {
                Date createTimeBegin = sdf.parse(createTimeBeginDate);
                param.put("createTimeBeginDate", createTimeBegin);
            }
            if (StringUtils.isNotEmpty(createTimeEndDate)) {
                Date createTimeEnd = sdf.parse(createTimeEndDate);
                param.put("createTimeEndDate", createTimeEnd);
            }
            if (StringUtils.isNotEmpty(releaseTimeBeginDate)) {
                Date releaseTimeBegin = sdf.parse(releaseTimeBeginDate);
                param.put("releaseTimeBeginDate", releaseTimeBegin);
            }
            if (StringUtils.isNotEmpty(releaseTimeEndDate)) {
                Date releaseTimeEnd = sdf.parse(releaseTimeEndDate);
                param.put("releaseTimeEndDate", releaseTimeEnd);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        List<BigStyleAnalyseMaster> data = bigStyleAnalyseMasterService.getDataByPager(param);
        if (CollUtil.isEmpty(data)) {
            return Result.ok("无数据导出");
        }
        parseStatus(data);
        XSSFWorkbook wb = null;
        ServletOutputStream fos = null;
        try {
            wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet();
            Row row = sheet.createRow(0);
            int cellIndex = 0;
            Cell cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("款式工艺路线编码");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("款式描述");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("工艺主框架");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("标准时间");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("标准单价");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("服装品类");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("创建人");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("创建时间");
            int rowIndex = 1;
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (BigStyleAnalyseMaster big : data) {
                row = sheet.createRow(rowIndex++);
                cellIndex = 0;
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getBigStyleAnalyseCode());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getStyleDesc());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getMainFrameName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (null != big.getStandardTime()) {
                    cell.setCellValue(big.getStandardTime().toPlainString());
                }
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (null != big.getStandardPrice()) {
                    cell.setCellValue(big.getStandardPrice().toPlainString());
                }
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getClothesCategoryName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                String user = big.getCreateUser();
                if (userMap.containsKey(user)) {
                    cell.setCellValue(userMap.get(user));
                } else {
                    cell.setCellValue("");
                }
                cell = row.createCell(cellIndex++, CellType.STRING);
                Date createTime = big.getCreateTime();
                String time = "";
                if (null != createTime) {
                    time = sdf1.format(createTime);
                }
                cell.setCellValue(time);
            }
            String fileName = URLEncoder.encode("款式工艺清单.xlsx", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            fos = response.getOutputStream();
            wb.write(fos);
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                fos.close();
                fos = null;
            }
            if (null != wb) {
                wb.close();
                wb = null;
            }
        }
        if (null != data) {
            data.clear();
            data = null;
        }
        return null;
    }

    private static void insertPictureToExcel(String picUrl, String path, Sheet sheet, Workbook wb, int a, int b,
                                             int c, int d) {
        if (StringUtils.isEmpty(picUrl)) {
            return;
        }
        String imageType = ".png";
        if (picUrl != null && !picUrl.isEmpty() && picUrl.lastIndexOf(".") != -1) {// 取图片的后缀
            imageType = picUrl.substring(picUrl.lastIndexOf("."));
        }

        try {
            URL url = new URL(picUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setConnectTimeout(1000 * 5);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            // 文件大小
            InputStream inputStream = httpURLConnection.getInputStream();
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, fileOut);
            Drawing patriarch = sheet.createDrawingPatriarch();
            /**
             * 该构造函数有8个参数 前四个参数是控制图片在单元格的位置，分别是图片距离单元格left，top，right，bottom的像素距离
             * 后四个参数，前连个表示图片左上角所在的cellNum和 rowNum，后天个参数对应的表示图片右下角所在的cellNum和 rowNum，
             * excel中的cellNum和rowNum的index都是从0开始的
             *int a, int b, int col2, int row2
             */
            // 图片一导出到单元格B2中
            XSSFClientAnchor anchor = new XSSFClientAnchor(Units.EMU_PER_PIXEL,
                    Units.EMU_PER_PIXEL, Units.EMU_PER_PIXEL * (-1), Units.EMU_PER_PIXEL * (-1),
                    a, b, c, d);
            // anchor.setAnchorType(XSSFClientAnchor.MOVE_AND_RESIZE);
            patriarch.createPicture(anchor, wb.addPicture(fileOut.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG));
            if (null != inputStream) {
                inputStream.close();
                inputStream = null;
            }
            if (null != fileOut) {
                fileOut.close();
                fileOut = null;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void doneForPage() {
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        List<WebConfig> webConfigList = webConfigService.getConfigList();
    }

    @ApiOperation(value = "/exportBigStyleCraftPath", notes = "导出单个款式工艺路径清单", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportBigStyleCraftPath", method = RequestMethod.GET)
    public Result exportBigStyleCraftPath(@RequestParam(name = "randomCode", required = true) String randomCode,
                                          @RequestParam(name = "type", required = true) Integer type,
                                          HttpServletResponse response) throws Exception {
        if (StringUtils.isEmpty(randomCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "参数为空");
        }
        doneForPage();
        Long ran = Long.parseLong(randomCode);
        BigStyleAnalyseMaster data = bigStyleAnalyseMasterService.searchFromBigStyleAnalyseByRandomCodeWithOutPartDeatil(ran);
        if (null == data) {
            return Result.ok("无数据导出");
        }
        List<CraftVO> craftVOS = new ArrayList<>();
        if (10 == type || 20 == type) {
            craftVOS = styleSewingCraftWarehouseService.getDataForExcelReport(ran);

            for (int i = 1; i < craftVOS.size(); i++) {
                craftVOS.get(i - 1).setNextCraftCode(craftVOS.get(i).getCraftCode());
            }

            Map<String, Integer> productionPartMaxFlowMap = new HashMap<>();
            Map<String, String> productionPartMaxCraftMap = new HashMap<>();
            Map<String, Integer> productionPartMinFlowMap = new HashMap<>();
            Map<String, String> productionPartMinCraftMap = new HashMap<>();
            Map<String, CraftVO> craftVoMap = new HashMap<>();
            List<FlowNumConfig> flowNumConfigs = productionPartService.getFlowNumConfigAll();

            for (CraftVO craft : craftVOS) {
                FlowNumConfig flowNumConfig = flowNumConfigs.stream().filter(x -> craft.getCraftFlowNum() != null
                        && craft.getCraftFlowNum().toString().startsWith(x.getFlowNum())).findFirst().orElse(null);
                if (flowNumConfig == null) {
                    continue;
                }
                String productionPartCode = flowNumConfig.getProductionPartCode();

                craft.setProductionPartCode(productionPartCode);
                int lastMax = productionPartMaxFlowMap.getOrDefault(productionPartCode, -1);
                int lastMin = productionPartMinFlowMap.getOrDefault(productionPartCode, -1);


                if (craft.getCraftFlowNum().intValue() > lastMax || lastMax == -1) {
                    productionPartMaxFlowMap.put(productionPartCode, craft.getCraftFlowNum());
                    productionPartMaxCraftMap.put(productionPartCode, craft.getCraftCode());
                }
                if (craft.getCraftFlowNum().intValue() < lastMin || lastMin == -1) {
                    productionPartMinFlowMap.put(productionPartCode, craft.getCraftFlowNum());
                    productionPartMinCraftMap.put(productionPartCode, craft.getCraftCode());
                }
                craftVoMap.put(productionPartCode + craft.getCraftCode(), craft);
            }

            List<CraftMainFrameRoute> routes = craftMainFrameRouteService.getMainFrameRouteByCode(data.getMainFrameCode());
            for (CraftMainFrameRoute route : routes) {
                String currCraftCode = productionPartMaxCraftMap.getOrDefault(route.getProductionPartCode(), null);
                String nextCraftCode = productionPartMinCraftMap.getOrDefault(route.getNextProductionPartCode(), null);
                if (currCraftCode == null || nextCraftCode == null) {
                    continue;
                }
                CraftVO craftVO = craftVoMap.getOrDefault(route.getProductionPartCode() + currCraftCode, null);
                if (craftVO == null) {
                    continue;
                }
                craftVO.setNextCraftCode(nextCraftCode);
            }

            Map<String, Integer> nexCraftMap = new HashMap<>();
            for (CraftVO craftVO : craftVOS) {
                if (craftVO.getNextCraftCode() == null) {
                    continue;
                }
                Integer n = nexCraftMap.getOrDefault(craftVO.getNextCraftCode(), 0);
                n += 1;
                nexCraftMap.put(craftVO.getNextCraftCode(), n);
            }

            for (CraftVO craftVO : craftVOS) {
                Integer n = 0;
                if (craftVO.getNextCraftCode() != null) {
                    n = nexCraftMap.getOrDefault(craftVO.getNextCraftCode(), 0);
                }
                if (n >= 2) {
                    craftVO.setNextCraftCodeCount(n);
                }
            }
        } else if (30 == type || 40 == type) {
            craftVOS = styleSewingCraftWarehouseService.getDataForExcelReportOrderByWorkOrder(ran);
        }


        //List<CraftVO> craftVOS = styleSewingCraftWarehouseService.getDataForExcelReport(ran);
        List<BigStyleMasterData> list = bigStyleMasterDataService.getDataByStyleRandomCode("big_style_analyse_master_skc", ran);
        //经向弹性等级
        String warpElasticGrade = "";
        //纬向弹性等级
        String weftElasticGrade = "";
        if (null != list && list.size() > 0) {
            warpElasticGrade = list.get(0).getWarpElasticGrade();
            weftElasticGrade = list.get(0).getWeftElasticGrade();
        }
        try {
            ExportBigDataUtil.exportData(type, null, data.getStyleDesc(), data.getBigStyleAnalyseCode(), null, data.getSubBrand(),
                    response, craftVOS, dictionaryService,
                    data.getPictures(), data.getCreateUserName(), data.getCreateTime(), data.getReleaseUserName(), data.getReleaseTime(), weftElasticGrade, warpElasticGrade);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 产生合并单元格赋值
     * CellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol)
     */
    private void doRegionCell(Sheet sheet, Row row, CellStyle style, int cellNumber, String value,
                              int a, int b, int c, int d, boolean hasThin) {
        CellRangeAddress row3Address1 = new CellRangeAddress(a, b, c, d);
        sheet.addMergedRegionUnsafe(row3Address1);
        if (hasThin) {
            RegionUtil.setBorderBottom(BorderStyle.THIN, row3Address1, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, row3Address1, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, row3Address1, sheet);
            RegionUtil.setBorderTop(BorderStyle.THIN, row3Address1, sheet);
        }
        Cell row3cell2 = row.createCell(cellNumber, CellType.STRING);
        row3cell2.setCellStyle(style);
        row3cell2.setCellValue(value);
    }

    /**
     * 产生普通单元格赋值
     */
    private static void doCell(Row row, CellStyle style, int cellNumber, String value) {
        Cell row3cell2 = row.createCell(cellNumber, CellType.STRING);
        row3cell2.setCellStyle(style);
        row3cell2.setCellValue(value);
    }

    /**
     * 取到普通单元格赋值
     */
    private void getCellThenSetValue(Row row, int cellNumber, String value, CellStyle style) {
        Cell cell = row.getCell(cellNumber);
        if (null != style) {
            cell.setCellStyle(style);
        }
        cell.setCellValue(value);
    }

    private void parseStatus(List<BigStyleAnalyseMaster> data) {
        if (CollUtil.isEmpty(data)) {
            return;
        }
        data.stream().parallel().forEach(x -> {
            x.setStatusName(SewingCraftWarehouseController.getStatusName(x.getStatus()));
        });
    }

    @RequestMapping(value = "/insertByHistoryBigStyle", method = RequestMethod.POST)
    @ApiOperation(value = "insertByHistoryBigStyle", notes = "修改款式状态")
    @Authentication(auth = Authentication.AuthType.EDIT, required = false)
    public Result<BigStyleAnalyseMaster> insertByHistoryBigStyle(
            @RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
            @RequestParam(name = "styleCode", required = true) String styleCode) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("styleAnalyseCode", styleCode);
        param.put("userCode", (currentUser != null ? currentUser.getUserCode() : ""));
        param.put("userName", (currentUser != null ? currentUser.getUserName() : ""));
        param.put("user", (currentUser != null ? currentUser.getUser() : ""));


        int ret = bigStyleAnalyseMasterService.insertByHistoryBigStyle(param);
        if (ret > 0) {
            BigStyleAnalyseMaster bigStyle = (BigStyleAnalyseMaster) param.get("bigStyle");
            return Result.ok(bigStyle);
        } else {
            String err = (String) param.getOrDefault("err", "");
            return Result.build(-1, err);
        }
    }

}
