package com.ylzs.controller.bigticketno;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.controller.bigstylecraft.ExportBigDataUtil;
import com.ylzs.controller.sewingcraft.SewingCraftWarehouseController;
import com.ylzs.dao.bigticketno.BigOrderMasterDao;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigticketno.FmsTicketNo;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.entity.craftmainframe.FlowNumConfig;
import com.ylzs.entity.custom.CustomStyleCraftCourse;
import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.entity.plm.BigStyleMasterData;
import com.ylzs.entity.system.User;
import com.ylzs.service.bigstylecraft.BigStyleAnalyseMasterService;
import com.ylzs.service.bigstylecraft.BigStyleOperationLogService;
import com.ylzs.service.bigticketno.BigOrderMasterService;
import com.ylzs.service.bigticketno.BigOrderSewingCraftWarehouseService;
import com.ylzs.service.bigticketno.FmsTicketNoService;
import com.ylzs.service.craftmainframe.ICraftMainFrameRouteService;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.service.craftmainframe.IProductionPartService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.custom.ICustomStyleCraftCourseService;
import com.ylzs.service.factory.IProductionGroupService;
import com.ylzs.service.plm.IBigStyleMasterDataService;
import com.ylzs.service.system.IUserService;
import com.ylzs.service.timeparam.CraftGradeEquipmentService;
import com.ylzs.vo.bigstylereport.BigStyleVO;
import com.ylzs.vo.bigstylereport.CraftVO;
import com.ylzs.vo.craftstd.FactoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xuwei
 * @create 2020-06-10 10:10
 */
@RestController
@RequestMapping("/fmsTicketNo")
@Api(tags = "??????")
public class FmsTicketNoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmsTicketNoController.class);
    @Resource
    private FmsTicketNoService fmsTicketNoService;
    @Resource
    private IDictionaryService dictionaryService;
    @Resource
    private CraftGradeEquipmentService craftGradeEquipmentService;

    @Resource
    private BigOrderMasterService bigOrderMasterService;

    @Resource
    private ICraftMainFrameService mainFrameService;

    @Resource
    private IUserService userService;

    @Resource
    private IProductionGroupService productionGroupService;

    @Resource
    private BigOrderSewingCraftWarehouseService bigOrderSewingCraftWarehouseService;

    @Resource
    private ICustomStyleCraftCourseService customStyleCraftCourseService;

    @Resource
    private IBigStyleMasterDataService bigStyleMasterDataService;

    @Resource
    private BigStyleAnalyseMasterService bigStyleAnalyseMasterService;

    @Resource
    private BigStyleOperationLogService bigStyleOperationLogService;

    @Resource
    private ICraftMainFrameRouteService craftMainFrameRouteService;

    @Resource
    private IProductionPartService productionPartService;

    @Resource
    private BigOrderMasterDao bigOrderMasterDao;


    @RequestMapping(value = "/getTicketList", method = RequestMethod.GET)
    @ApiOperation(value = "getTicketList", notes = "?????????????????????", httpMethod = "GET", response = Result.class)
    public Result<List<FmsTicketNo>> getTicketList(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "30") Integer rows,
                                                   @RequestParam(name = "productionTicketNo", required = false) String productionTicketNo,
                                                   @RequestParam(name = "mtmOrder", required = false) String mtmOrder,
                                                   @RequestParam(name = "customStyleCode", required = false) String customStyleCode,
                                                   @RequestParam(name = "styleCode", required = false) String styleCode,
                                                   @RequestParam(name = "styleCodeColor", required = false) String styleCodeColor,
                                                   @RequestParam(name = "factoryCode", required = false) String factoryCode,
                                                   @RequestParam(name = "productionCategory", required = false) String productionCategory,
                                                   @RequestParam(name = "productionCategoryName", required = false) String productionCategoryName,
                                                   @RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                                                   @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate,
                                                   @RequestParam(name = "styleType", required = false) String styleType) {
        PageHelper.startPage(page, rows);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(productionTicketNo)) {
            map.put("productionTicketNo", productionTicketNo);
        }
        if (StringUtils.isNotEmpty(productionCategoryName)) {
            map.put("productionCategoryName", productionCategoryName);
        }
        if (StringUtils.isNotEmpty(customStyleCode)) {
            map.put("customStyleCode", customStyleCode);
        }
        if (StringUtils.isNotEmpty(styleCodeColor)) {
            map.put("styleCodeColor", styleCodeColor);
        }
        if (StringUtils.isNotEmpty(mtmOrder)) {
            map.put("mtmOrder", mtmOrder);
        }
        if (StringUtils.isNotEmpty(styleCode)) {
            map.put("styleCode", styleCode);
        }
        if (StringUtils.isNotEmpty(styleType)) {
            map.put("styleType", styleType);
        }
        if (StringUtils.isNotEmpty(factoryCode)) {
            map.put("factoryCode", factoryCode);
        }
        if (StringUtils.isNotEmpty(productionCategory)) {
            map.put("productionCategory", productionCategory);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isNotEmpty(createTimeBeginDate)) {
                Date createTimeBegin = sdf.parse(createTimeBeginDate);
                map.put("createTimeBeginDate", createTimeBegin);
            }
            if (StringUtils.isNotEmpty(createTimeEndDate)) {
                Date createTimeEnd = sdf.parse(createTimeEndDate);
                map.put("createTimeEndDate", createTimeEnd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<FmsTicketNo> data = fmsTicketNoService.getAll(map);
        Map<String, ProductionGroup> groupMap = productionGroupService.getAllToMap();
        if (null != data && data.size() > 0) {
            for (FmsTicketNo group : data) {
                if (groupMap.containsKey(group.getProductionCategory())) {
                    group.setProductionCategoryName(groupMap.get(group.getProductionCategory()).getProductionGroupName());
                }
            }
        }

        Map<String, BigStyleAnalyseMaster> allReleaseUserMap = bigStyleAnalyseMasterService.findAllReleaseUser();
        if (null != allReleaseUserMap && allReleaseUserMap.size() > 0 && null != data && data.size() > 0) {
            for (FmsTicketNo fmsTicketNo : data) {
                if (allReleaseUserMap.containsKey(fmsTicketNo.getStyleCodeColor())) {
                    fmsTicketNo.setAllocationUser("");
                    if (StringUtils.isNotBlank(allReleaseUserMap.get(fmsTicketNo.getStyleCodeColor()).getUpdateUserName())) {
                        fmsTicketNo.setUpdateUserName(allReleaseUserMap.get(fmsTicketNo.getStyleCodeColor()).getUpdateUserName());
                    }
                    if (StringUtils.isNotBlank(allReleaseUserMap.get(fmsTicketNo.getStyleCodeColor()).getReleaseUserName())) {
                        fmsTicketNo.setReleaseUserName(allReleaseUserMap.get(fmsTicketNo.getStyleCodeColor()).getReleaseUserName());
                    }

                }
            }
        }

        PageInfo<FmsTicketNo> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getAllFactorys", method = RequestMethod.GET)
    @ApiOperation(value = "getAllFactorys", notes = "???????????????????????????", httpMethod = "GET", response = Result.class)
    public Result<List<FactoryVO>> getAllFactorys() {
        List<FactoryVO> list = craftGradeEquipmentService.getAllFactory();
        return Result.build(MessageConstant.SUCCESS, "??????", list);
    }

    @RequestMapping(value = "/getBigStyleAnalyseRandomCode", method = RequestMethod.GET)
    @ApiOperation(value = "getBigStyleAnalyseRandomCode", notes = "??????????????????????????????????????????", httpMethod = "GET", response = Result.class)
    public Result<BigStyleVO> getBigStyleAnalyseRandomCode(@RequestParam(name = "productionTicketNo", required = true) String productionTicketNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("productionTicketNo", productionTicketNo);
        List<FmsTicketNo> ticketNos = fmsTicketNoService.getAll(param);
        if (null != ticketNos && ticketNos.size() > 0) {
            FmsTicketNo no = ticketNos.get(0);
            //????????????
            if (StringUtils.isNotEmpty(no.getMtmOrder())) {
                List<CustomStyleCraftCourse> courses = customStyleCraftCourseService.getOrderCustmStyleBaseList(no.getMtmOrder(), no.getOrderLineId());
                if (null == courses || courses.size() == 0) {
                    return Result.build(MessageConstant.DataNotExistException, "??????????????????");
                }
                CustomStyleCraftCourse course = courses.get(0);

                BigStyleVO vo = new BigStyleVO();
                vo.setOrderNo(no.getMtmOrder());
                vo.setType(BusinessConstants.OrderType.CUSTOMER_OIRDER);
                vo.setOrderRandomCode(course.getRandomCode());
                vo.setCustomOrderNo(no.getMtmOrder() + "-" + course.getOrderLineId());
                vo.setOrderLineId(course.getOrderLineId());
                return Result.build(MessageConstant.SUCCESS, "??????", vo);
            } else {
                List<BigStyleVO> list = fmsTicketNoService.getBigStyleAnalyseByTicketNo(productionTicketNo);
                if (null == list || list.size() == 0) {
                    return Result.build(MessageConstant.DataNotExistException, "?????????????????????????????????????????????");
                }
                BigStyleVO vo = list.get(0);
                vo.setType(BusinessConstants.OrderType.BIG_ORDER);
                List<BigStyleAnalyseMaster> orders = bigOrderMasterDao.searchFromBigStyleAnalyse(param);
                if (null != orders && orders.size() > 0) {
                    return Result.build(301, "??????????????????????????????????????????", vo);
                }
                if (!BusinessConstants.Status.PUBLISHED_STATUS.equals(vo.getStatus())) {
                    return Result.build(MessageConstant.DataNotExistException, "?????????????????????????????????????????????");
                }
                return Result.build(MessageConstant.SUCCESS, "??????", vo);
            }
        }
        return Result.build(MessageConstant.DataNotExistException, "????????????????????????????????????");
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "updateStatus", notes = "??????????????????")
    public Result<JSONObject> updateStatus(@RequestParam(name = "randomCode", required = true) String randomCode,
                                           @RequestParam(name = "status", required = true) Integer status,
                                           @RequestParam(name = "userCode", required = false) String userCode) throws Exception {
        Long randomCodeLong = Long.parseLong(randomCode);
        if (bigOrderMasterService.updateStatus(status, randomCodeLong, userCode)) {

            JSONObject obj = new JSONObject();
            obj.put("randomCode", randomCode);
            obj.put("status", status);
            return Result.ok(MessageConstant.SUCCESS, obj);
        }
        return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
    }

    @RequestMapping(value = "/getFmsTicketNo", method = RequestMethod.GET)
    @ApiOperation(value = "getFmsTicketNo", notes = "?????????????????????fms??????????????????", httpMethod = "GET", response = Result.class)
    public Result<FmsTicketNo> getFmsTicketNo(@RequestParam(name = "productionTicketNo", required = true) String productionTicketNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("productionTicketNo", productionTicketNo);
        List<FmsTicketNo> list = fmsTicketNoService.getAll(map);
        if (null == list || list.size() == 0) {
            return Result.build(MessageConstant.DataNotExistException, "???????????????????????????");
        }
        Map<String, ProductionGroup> groupMap = productionGroupService.getAllToMap();
        FmsTicketNo group = list.get(0);
        if (groupMap.containsKey(group.getProductionCategory())) {
            group.setProductionCategoryName(groupMap.get(group.getProductionCategory()).getProductionGroupName());
        }
        return Result.build(MessageConstant.SUCCESS, "??????", group);
    }

    @RequestMapping(value = "/getMainFrameCodeByProductionCategory", method = RequestMethod.GET)
    @ApiOperation(value = "getMainFrameCodeByProductionCategory", notes = "?????????????????????????????????", httpMethod = "GET", response = Result.class)
    public Result<JSONObject> getMainFrameCodeByProductionCategory(@RequestParam(name = "productionCategory", required = true) String productionCategory,
                                                                   @RequestParam(name = "craftCategoryCode", required = true) String craftCategoryCode) {
        ProductionGroup group = productionGroupService.getProductCodeByNameAndCraftCatetory(productionCategory, craftCategoryCode);
        JSONObject obj = new JSONObject();
        if (null != group) {
            obj.put("mainFrameCode", group.getMainFrameCode());
            obj.put("mainFrameName", group.getMainFrameName());
            obj.put("productionCategory", productionCategory);
        }
        return Result.build(MessageConstant.SUCCESS, "??????", obj);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "??????????????????????????????")
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
                                                      @RequestParam(name = "releaseTimeEndDate", required = false) String releaseTimeEndDate,
                                                      @RequestParam(name = "factoryCode", required = false) String factoryCode,
                                                      @RequestParam(name = "adaptCode", required = false) String adaptCode,
                                                      @RequestParam(name = "productionCategory", required = false) String productionCategory,
                                                      @RequestParam(name = "productionCategoryName", required = false) String productionCategoryName,
                                                      @RequestParam(name = "productionTicketNo", required = false) String productionTicketNo,
                                                      @RequestParam(name = "styleCode", required = false) String styleCode) {

        LOGGER.info("???????????????????????????: page" + page + " rows:" + rows + " mainFrameCode:" + mainFrameCode);
        Map<String, Object> param = new HashMap<>();
        param.put("mainFrameCode", mainFrameCode);
        if (StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            bigStyleAnalyseCode = StringUtils.replaceBlank(bigStyleAnalyseCode);
        }
        if (StringUtils.isNotEmpty(productionCategoryName)) {
            productionCategoryName = StringUtils.replaceBlank(productionCategoryName);
        }
        if (StringUtils.isNotEmpty(styleDesc)) {
            styleDesc = StringUtils.replaceBlank(styleDesc);
        }
        if (StringUtils.isNotEmpty(productionCategory)) {
            productionCategory = StringUtils.replaceBlank(productionCategory);
        }
        if (StringUtils.isNotEmpty(styleCode)) {
            styleCode = StringUtils.replaceBlank(styleCode);
        }
        param.put("styleCode", styleCode);
        param.put("adaptCode", adaptCode);
        final Map<String, ProductionGroup> groupMap = productionGroupService.getAllToMap();
        List<String> productionCategoryList = new ArrayList<>();
        if (StringUtils.isNotEmpty(productionCategoryName)) {
            productionCategoryName = StringUtils.replaceBlank(productionCategoryName);
            Iterator<String> iterator = groupMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (groupMap.get(key).getProductionGroupName().indexOf(productionCategoryName) != -1) {
                    productionCategoryList.add(key);
                }
            }

        }
        param.put("productionCategoryName", productionCategoryName);
        if (StringUtils.isNotEmpty(productionTicketNo)) {
            productionTicketNo = StringUtils.replaceBlank(productionTicketNo);
        }
        param.put("productionTicketNo", productionTicketNo);
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
        param.put("factoryCode", factoryCode);
        param.put("productionCategory", productionCategoryList);
        param.put("releaseUser", releaseUserList);
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
        //PageHelper.startPage(page, rows);
        //???????????????????????????
        List<BigStyleAnalyseMaster> data = bigOrderMasterService.getDataByPager(param, productionCategoryName, groupMap);
        Long length = 0L;
        if (null != data) {
            length = (long) data.size();
        }
        param.put("page", (page - 1) * rows);
        param.put("rows", rows);
        List<BigStyleAnalyseMaster> data1 = bigOrderMasterService.getDataByPager(param, productionCategoryName, groupMap);
        //  PageInfo<BigStyleAnalyseMaster> pageInfo = new PageInfo<>(data);
        return Result.ok(data1, length);
    }

    @ApiOperation(value = "/exportBigOrder", notes = "??????????????????????????????", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportBigOrder", method = RequestMethod.GET)
    public Result exportBigOrder(@RequestParam(name = "mainFrameCode", required = false) String mainFrameCode,
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
                                 @RequestParam(name = "factoryCode", required = false) String factoryCode,
                                 @RequestParam(name = "adaptCode", required = false) String adaptCode,
                                 @RequestParam(name = "productionCategory", required = false) String productionCategory,
                                 @RequestParam(name = "productionCategoryName", required = false) String productionCategoryName,
                                 @RequestParam(name = "productionTicketNo", required = false) String productionTicketNo,
                                 @RequestParam(name = "styleCode", required = false) String styleCode,
                                 HttpServletResponse response) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("mainFrameCode", mainFrameCode);
        if (StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            bigStyleAnalyseCode = StringUtils.replaceBlank(bigStyleAnalyseCode);
        }
        if (StringUtils.isNotEmpty(productionCategoryName)) {
            productionCategoryName = StringUtils.replaceBlank(productionCategoryName);
        }
        if (StringUtils.isNotEmpty(styleDesc)) {
            styleDesc = StringUtils.replaceBlank(styleDesc);
        }
        if (StringUtils.isNotEmpty(productionCategory)) {
            productionCategory = StringUtils.replaceBlank(productionCategory);
        }
        if (StringUtils.isNotEmpty(styleCode)) {
            styleCode = StringUtils.replaceBlank(styleCode);
        }
        param.put("styleCode", styleCode);
        param.put("adaptCode", adaptCode);
        final Map<String, ProductionGroup> groupMap = productionGroupService.getAllToMap();
        List<String> productionCategoryList = new ArrayList<>();
        if (StringUtils.isNotEmpty(productionCategoryName)) {
            productionCategoryName = StringUtils.replaceBlank(productionCategoryName);
            Iterator<String> iterator = groupMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (groupMap.get(key).getProductionGroupName().indexOf(productionCategoryName) != -1) {
                    productionCategoryList.add(key);
                }
            }

        }
        param.put("productionCategoryName", productionCategoryName);
        if (StringUtils.isNotEmpty(productionTicketNo)) {
            productionTicketNo = StringUtils.replaceBlank(productionTicketNo);
        }
        param.put("productionTicketNo", productionTicketNo);
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
        param.put("factoryCode", factoryCode);
        param.put("productionCategory", productionCategoryList);
        param.put("releaseUser", releaseUserList);
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
        List<BigStyleAnalyseMaster> data = bigOrderMasterService.getDataByPager(param, productionCategoryName, groupMap);
        if (CollUtil.isEmpty(data)) {
            return Result.ok("???????????????");
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
            cell.setCellValue("???????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("??????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("???????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("??????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("?????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("?????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            int rowIndex = 1;
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (BigStyleAnalyseMaster big : data) {
                row = sheet.createRow(rowIndex++);
                cellIndex = 0;
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getProductionTicketNo());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getCtStyleCode());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getStyleDesc());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getMainFrameName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getProductionCategoryName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getStatusName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getClothesCategoryName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (null != big.getStandardTime()) {
                    cell.setCellValue(big.getStandardTime().toPlainString());
                }
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (null != big.getStandardPrice()) {
                    cell.setCellValue(big.getStandardPrice().toPlainString());
                }
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getCreateUserName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (big.getCreateTime() != null) {
                    cell.setCellValue(time.format(big.getCreateTime()));
                }
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getReleaseUserName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (big.getReleaseTime() != null) {
                    cell.setCellValue(time.format(big.getReleaseTime()));
                }

            }
            String fileName = URLEncoder.encode("??????????????????.xlsx", "UTF-8");
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

    @ApiOperation(value = "/exportTicket", notes = "??????FMS?????????????????????", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportTicket", method = RequestMethod.GET)
    public Result exportTicket(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "30") Integer rows,
                               @RequestParam(name = "productionTicketNo", required = false) String productionTicketNo,
                               @RequestParam(name = "mtmOrder", required = false) String mtmOrder,
                               @RequestParam(name = "customStyleCode", required = false) String customStyleCode,
                               @RequestParam(name = "styleCode", required = false) String styleCode,
                               @RequestParam(name = "styleCodeColor", required = false) String styleCodeColor,
                               @RequestParam(name = "factoryCode", required = false) String factoryCode,
                               @RequestParam(name = "productionCategory", required = false) String productionCategory,
                               @RequestParam(name = "productionCategoryName", required = false) String productionCategoryName,
                               @RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                               @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate,
                               @RequestParam(name = "styleType", required = false) String styleType,
                               HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(productionTicketNo)) {
            map.put("productionTicketNo", productionTicketNo);
        }
        if (StringUtils.isNotEmpty(productionCategoryName)) {
            map.put("productionCategoryName", productionCategoryName);
        }
        if (StringUtils.isNotEmpty(customStyleCode)) {
            map.put("customStyleCode", customStyleCode);
        }
        if (StringUtils.isNotEmpty(mtmOrder)) {
            map.put("mtmOrder", mtmOrder);
        }
        if (StringUtils.isNotEmpty(styleCode)) {
            map.put("styleCode", styleCode);
        }
        if (StringUtils.isNotEmpty(styleType)) {
            map.put("styleType", styleType);
        }
        if (StringUtils.isNotEmpty(factoryCode)) {
            map.put("factoryCode", factoryCode);
        }
        if (StringUtils.isNotEmpty(productionCategory)) {
            map.put("productionCategory", productionCategory);
        }
        if (StringUtils.isNotBlank("styleCodeColor")) {
            map.put("styleCodeColor", styleCodeColor);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isNotEmpty(createTimeBeginDate)) {
                Date createTimeBegin = sdf.parse(createTimeBeginDate);
                map.put("createTimeBeginDate", createTimeBegin);
            }
            if (StringUtils.isNotEmpty(createTimeEndDate)) {
                Date createTimeEnd = sdf.parse(createTimeEndDate);
                map.put("createTimeEndDate", createTimeEnd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<FmsTicketNo> data = fmsTicketNoService.getAll(map);
        if (CollUtil.isEmpty(data)) {
            return Result.ok("???????????????");
        }
        XSSFWorkbook wb = null;
        ServletOutputStream fos = null;
        List<FactoryVO> list = craftGradeEquipmentService.getAllFactory();
        Map<String, String> factoryMap = new HashMap<>();
        for (FactoryVO vo : list) {
            factoryMap.put(vo.getFactoryCode(), vo.getFactoryName());
        }
        try {
            wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet();
            Row row = sheet.createRow(0);
            int cellIndex = 0;
            Cell cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("???????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("??????????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("??????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("???????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("??????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            cell = row.createCell(cellIndex++, CellType.STRING);
            cell.setCellValue("????????????");
            int rowIndex = 1;
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (FmsTicketNo big : data) {
                row = sheet.createRow(rowIndex++);
                cellIndex = 0;
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getProductionTicketNo());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getStyleCodeColor());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getNumber());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getMtmOrder());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getCustomStyleCode());
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(factoryMap.get(big.getFactoryCode()));
                cell = row.createCell(cellIndex++, CellType.STRING);
                cell.setCellValue(big.getProductionCategoryName());
                cell = row.createCell(cellIndex++, CellType.STRING);
                if (null != big.getCreateTime()) {
                    cell.setCellValue(time.format(big.getCreateTime()));
                }
            }
            String fileName = URLEncoder.encode("????????????.xlsx", "UTF-8");
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

    private void parseStatus(List<BigStyleAnalyseMaster> data) {
        if (CollUtil.isEmpty(data)) {
            return;
        }
        data.stream().parallel().forEach(x -> {
            x.setStatusName(SewingCraftWarehouseController.getStatusName(x.getStatus()));
        });
    }

    @RequestMapping(value = "/checkIsReferenced", method = RequestMethod.GET)
    @ApiOperation(value = "checkIsReferenced", notes = "??????????????????????????????????????????????????????")
    public Result<JSONObject> checkIsReferenced(@RequestParam(name = "bigStyleAnalyseCode", required = true) String bigStyleAnalyseCode) {
        if (StringUtils.isEmpty(bigStyleAnalyseCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        JSONObject result = new JSONObject();
        Map<String, Object> param = new HashMap<>();
        param.put("bigStyleAnalyseCode", bigStyleAnalyseCode);
        List<BigStyleAnalyseMaster> list = bigOrderMasterService.searchFromBigStyleAnalyse(param);
        if (list != null && list.size() > 0) {
            result.put("referenced", "Y");
        } else {
            result.put("referenced", "N");
        }
        return Result.ok(MessageConstant.SUCCESS, result);
    }

    @RequestMapping(value = "/getBigOrderByRandomCode", method = RequestMethod.GET)
    @ApiOperation(value = "getBigOrderByRandomCode", notes = "??????randomCode??????????????????")
    public Result<BigStyleAnalyseMaster> getBigOrderByRandomCode(@RequestParam(name = "randomCode", required = true) String randomCode) {
        LOGGER.info("?????????randomCode???" + randomCode);
        if (StringUtils.isEmpty(randomCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        Long ran = Long.parseLong(randomCode);
        BigStyleAnalyseMaster data = bigOrderMasterService.searchFromBigStyleAnalyseByRandomCode(ran);
        if (null != data) {
            Map<String, Object> param = new HashMap<>();
            param.put("productionTicketNo", data.getProductionTicketNo());
            List<FmsTicketNo> list = fmsTicketNoService.getAll(param);
            if (null != list && list.size() > 0) {
                data.setNumber(list.get(0).getNumber());
            }
        }
        return Result.ok(MessageConstant.SUCCESS, data);
    }

    /**
     * ??????????????????
     */
    @RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "addOrUpdate", notes = "??????????????????")
    public Result<JSONObject> addOrUpdate(HttpServletRequest request) throws Exception {
        String data = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        if (StringUtils.isEmpty(data)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        // LogUtil.insertBigStyleOperationToLOG(data, "big_order_master");
        JSONObject result = new JSONObject();
        LOGGER.info("????????????????????????????????????" + data);
        JSONObject dataObj = JSONObject.parseObject(data);
        //????????????
        try {
            Result<JSONObject> paramCheckResult = checkParam(dataObj);
            if (MessageConstant.PARAM_NULL.equals(paramCheckResult.getCode())) {
                LOGGER.error("???????????????" + JSONObject.toJSONString(paramCheckResult));
                return paramCheckResult;
            }
        } catch (Exception e) {
            Result.ok(MessageConstant.SERVER_FIELD_ERROR, result.put("msg", e.getMessage()));
        }

        //randomCode
        String randomCode = dataObj.getString("randomCode");
        String operation = BusinessConstants.Send2Pi.actionType_create;
        if (StringUtils.isNotEmpty(randomCode)) {
            LOGGER.info("------?????????????????????????????????update--------");
            operation = BusinessConstants.Send2Pi.actionType_update;
        }
        String userCode = dataObj.getString("userCode");
        String productionTicketNo = dataObj.getString("productionTicketNo");
        LOGGER.error(userCode + "--" + randomCode + "--" + productionTicketNo + "--" + operation + "--" + "big_order_master");
        result = bigOrderMasterService.addOrUpdate(dataObj, operation);
        if (null != result && StringUtils.isNotEmpty(result.getString("bigStyleAnalyseCode")) && result.getLong("randomCode") != null) {
            return Result.ok(MessageConstant.SUCCESS, result);
        } else {
            return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
        }
    }

    /**
     * ????????????
     * operationType ?????????????????????add???update????????????????????????update??????????????????randomCode???Id?????????
     */
    public Result<JSONObject> checkParam(JSONObject dataObj) throws Exception {
        //????????????
        String userCode = dataObj.getString("userCode");
        if (StringUtils.isEmpty(userCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        //????????????
        String status = dataObj.getString("status");
        if (StringUtils.isEmpty(status)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        /*String partCrafts = dataObj.getString("partCraftList");
        List<BigStyleAnalysePartCraftDetail> partCraftDetailList = JSONObject.parseArray(partCrafts, BigStyleAnalysePartCraftDetail.class);
        if (null == partCraftDetailList || partCraftDetailList.size() == 0) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }*/
        //????????????
        String styleName = dataObj.getString("styleName");
        if (StringUtils.isEmpty(styleName)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        //????????????
        String ctStyleCode = dataObj.getString("ctStyleCode");
        if (StringUtils.isEmpty(ctStyleCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }

        //??????--?????????
        String subBrand = dataObj.getString("subBrand");
        if (StringUtils.isEmpty(subBrand)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        //????????????
        String clothesCategoryCode = dataObj.getString("clothesCategoryCode");
        if (StringUtils.isEmpty(clothesCategoryCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????????????????");
        }
        if (StringUtils.isEmpty(dataObj.getString("forSalesTime"))) {
            dataObj.put("forSalesTime", null);
        }
        //????????????
        String fabricFraction = dataObj.getString("fabricFraction");
        if (StringUtils.isEmpty(fabricFraction)) {
            return Result.build(MessageConstant.PARAM_NULL, "??????????????????");
        }
        //?????????????????????
        String mainFrameCode = dataObj.getString("mainFrameCode");
        if (StringUtils.isEmpty(mainFrameCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "???????????????????????????");
        }
        return Result.build(MessageConstant.SUCCESS, "????????????OK");
    }

    @ApiOperation(value = "/exportOrderPath", notes = "????????????????????????????????????", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportOrderPath", method = RequestMethod.GET)
    public Result exportOrderPath(@RequestParam(name = "randomCode", required = true) String randomCode,
                                  @RequestParam(name = "type", required = true) Integer type,
                                  HttpServletResponse response) throws Exception {
        if (StringUtils.isEmpty(randomCode)) {
            return Result.build(MessageConstant.PARAM_NULL, "????????????");
        }
        Long ran = Long.parseLong(randomCode);
        BigStyleAnalyseMaster data = bigOrderMasterService.searchFromBigStyleAnalyseByRandomCodeWithOutPartDeatil(ran);
        if (null == data) {
            return Result.ok("???????????????");
        }
        List<CraftVO> craftVOS = new ArrayList<>();
        if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.NOT_HAVE_PRICE.equals(type)) {
            craftVOS = bigOrderSewingCraftWarehouseService.getDataForExcelReport(ran);

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
        } else if (BusinessConstants.ExportTemplate.EPS_HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.EPS_NOT_HAVE_PRICE.equals(type)) {
            craftVOS = bigOrderSewingCraftWarehouseService.getDataForExcelReportOrderByWorkOrder(ran);
        } else if (BusinessConstants.ExportTemplate.FINANCE.equals(type)) {
            craftVOS = bigOrderSewingCraftWarehouseService.getDataForFinanceByWorkOrder(ran);
        }


        //List<CraftVO> craftVOS = bigOrderSewingCraftWarehouseService.getDataForExcelReport(ran);
        Map<String, ProductionGroup> groupMap = productionGroupService.getAllToMap();
        String productionCategoryName = "";
        if (groupMap.containsKey(data.getProductionCategory())) {
            productionCategoryName = groupMap.get(data.getProductionCategory()).getProductionGroupName();
        }
        List<BigStyleMasterData> list = bigStyleMasterDataService.getDataByStyleRandomCode("big_style_analyse_master_skc", data.getStyleRandomCode());
        //??????????????????
        String warpElasticGrade = "";
        //??????????????????
        String weftElasticGrade = "";
        if (null != list && list.size() > 0) {
            warpElasticGrade = list.get(0).getWarpElasticGrade();
            weftElasticGrade = list.get(0).getWeftElasticGrade();
        }
        try {
            if (BusinessConstants.ExportTemplate.FINANCE.equals(type)) {
                ExportBigDataUtil.exportFinanceData(type, productionCategoryName, data.getStyleDesc(), data.getBigStyleAnalyseCode(), data.getProductionTicketNo(), data.getSubBrand(),
                        response, craftVOS, dictionaryService,
                        null, data.getCreateUserName(), data.getCreateTime(), data.getReleaseUserName(), data.getReleaseTime(), weftElasticGrade, warpElasticGrade);
            } else {
                ExportBigDataUtil.exportData(type, productionCategoryName, data.getStyleDesc(), data.getBigStyleAnalyseCode(), data.getProductionTicketNo(), data.getSubBrand(),
                        response, craftVOS, dictionaryService,
                        data.getPictures(), data.getCreateUserName(), data.getCreateTime(), data.getReleaseUserName(), data.getReleaseTime(), weftElasticGrade, warpElasticGrade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @RequestMapping(value = "/searchFromBigOrder", method = RequestMethod.GET)
    @ApiOperation(value = "searchFromBigOrder", notes = "????????????????????????", httpMethod = "GET", response = Result.class)
    public Result<List<BigStyleAnalyseMaster>> searchFromBigOrder(@RequestParam(defaultValue = "1") Integer page,
                                                                         @RequestParam(defaultValue = "30") Integer rows,
                                                                         @RequestParam(name = "productionTicketNo", required = false) String productionTicketNo,
                                                                         @RequestParam(name = "bigStyleAnalyseCode", required = false) String bigStyleAnalyseCode,
                                                                         @RequestParam(name = "styleDesc", required = false) String styleDesc,
                                                                         @RequestParam(name = "description", required = false) String description,
                                                                         @RequestParam(name = "craftCode", required = false) String craftCode,
                                                                         @RequestParam(name = "productionCategory", required = false) String productionCategory) {

        PageHelper.startPage(page, rows);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            bigStyleAnalyseCode = StringUtils.replaceBlank(bigStyleAnalyseCode);
            map.put("bigStyleAnalyseCode", bigStyleAnalyseCode);
        }
        if (StringUtils.isNotEmpty(styleDesc)) {
            styleDesc = StringUtils.replaceBlank(styleDesc);
            map.put("styleDesc", styleDesc);
        }
        if (StringUtils.isNotEmpty(productionTicketNo)) {
            productionTicketNo = StringUtils.replaceBlank(productionTicketNo);
            map.put("productionTicketNo", productionTicketNo);
        }
        if (StringUtils.isNotEmpty(productionCategory)) {
            productionCategory = StringUtils.replaceBlank(productionCategory);
            map.put("productionCategory", productionCategory);
        }
        if (StringUtils.isNotEmpty(craftCode)) {
            craftCode = StringUtils.replaceBlank(craftCode);
            map.put("craftCode", craftCode);
        }
        if (StringUtils.isNotEmpty(description)) {
            description = StringUtils.replaceBlank(description);
            map.put("description", description);
        }
        //??????????????????
        map.put("status", BusinessConstants.Status.PUBLISHED_STATUS);

        List<BigStyleAnalyseMaster> data = bigOrderMasterService.searchFromBigOrder(map);
        PageInfo<BigStyleAnalyseMaster> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());
    }
}
