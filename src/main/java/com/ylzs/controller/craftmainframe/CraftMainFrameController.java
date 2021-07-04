package com.ylzs.controller.craftmainframe;


import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.entity.craftmainframe.ProductionPart;
import com.ylzs.service.craftmainframe.ICraftMainFrameRouteService;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.service.craftmainframe.IProductionPartService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.craftstd.IMaxCodeService;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.craftmainframe.CraftMainFrameExportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.ylzs.common.constant.BusinessConstants.Status.*;
import static com.ylzs.common.util.Assert.*;

@Api(tags = "工艺主框架")
@RestController
@RequestMapping(value = "/craft-main-frame")
public class CraftMainFrameController implements IModuleInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(CraftMainFrameController.class);

    @Resource
    ICraftMainFrameService craftMainFrameService;

    @Resource
    IProductionPartService productionPartService;

    @Resource
    ICraftMainFrameRouteService craftMainFrameRouteService;

    @Resource
    IMaxCodeService maxCodeService;

    @Resource
    IOperationLogService operationLogService;

    @Resource
    IDictionaryService dictionaryService;


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/getDropDownData", method = RequestMethod.GET)
    @ApiOperation(value = "getDropDownData", notes = "查询工序主框架下拉信息")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<JSONObject> getDropDownData() {
        List<DictionaryVo> customProductionAreas = dictionaryService.getDictoryAll("CustomProductionArea");

        //返回结果
        JSONObject result = new JSONObject();
        result.put("customProductionAreas", customProductionAreas);

        return Result.ok(result);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询所有工艺主框架")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<CraftMainFrame>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                               @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                               @RequestParam(name = "keywords", required = false) String keywords,
                                               @RequestParam(name = "beginDate", required = false) Date beginDate,
                                               @RequestParam(name = "endDate", required = false) Date endDate) {
        PageHelper.startPage(page, rows);
        List<CraftMainFrame> craftMainFrames = craftMainFrameService.getByCondition(keywords, beginDate, endDate);
        PageInfo<CraftMainFrame> pageInfo = new PageInfo<>(craftMainFrames);
        return Result.ok(craftMainFrames, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getOne/{randomCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询单个工艺主框架")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<JSONObject> getOne(@PathVariable("randomCode") String randomCode) {
        notBlank(randomCode, "错误的工艺主框架关联代码");


        JSONObject result = new JSONObject();

        CraftMainFrame craftMainFrame = craftMainFrameService.selectByPrimaryKey(Long.parseLong(randomCode));
        notNull(craftMainFrame, "未找到工艺主框架");
        result.put("craftMainFrame", craftMainFrame);

        List<ProductionPart> productionParts = productionPartService.getByMainFrameRandomCode(craftMainFrame.getRandomCode());
        result.put("productionParts", productionParts);

        List<CraftMainFrameRoute> craftMainFrameRoutes = craftMainFrameRouteService.getByMainFrameRandomCode(craftMainFrame.getRandomCode());
        result.put("craftMainFrameRoutes", craftMainFrameRoutes);
        return Result.ok(result);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加工艺主框架")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<String> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                               @RequestBody CraftMainFrame craftMainFrame) {
        notBlank(craftMainFrame.getMainFrameName(), "工艺主框架名称不能为空");
        notNull(craftMainFrame.getCraftCategoryRandomCode(), "工艺品类关联代码不能为空");
        notBlank(craftMainFrame.getCraftCategoryCode(), "工艺品类代码不能为空");
        notBlank(craftMainFrame.getCraftCategoryName(), "工艺品类名称不能为空");
        if(craftMainFrame.getIsDefault() != null && craftMainFrame.getIsDefault()) {
            isFalse(craftMainFrameService.isDefaultMainFrameExists(craftMainFrame.getCraftCategoryRandomCode(), null),"该品类已存在默认主框架");
        }

        long randomCode = SnowflakeIdUtil.generateId();
        craftMainFrame.setRandomCode(randomCode);
        String preStr = craftMainFrame.getCraftCategoryCode().substring(0, 1) + "Z";
        String mainFrameCode = maxCodeService.getNextSerialNo(getModuleCode(), preStr, 2, false);
        craftMainFrame.setMainFrameCode(mainFrameCode);
        craftMainFrame.setCreateTime(new Date());
        craftMainFrame.setCreateUser(currentUser.getUserName());
        craftMainFrame.setCreateUserName(currentUser.getUserName());
        craftMainFrame.setUpdateTime(craftMainFrame.getCreateTime());
        craftMainFrame.setUpdateUser(currentUser.getUserName());
        craftMainFrame.setUpdateUserName(currentUser.getUserName());
        craftMainFrame.setStatus(DRAFT_STATUS);
        craftMainFrame.setIsInvalid(false);
        int ret = craftMainFrameService.insert(craftMainFrame);
        if(ret != 1) {
            return Result.build(-1,"保存失败");
        }
        return  Result.ok(String.valueOf(randomCode));
    }

    @RequestMapping(value = "/addAll", method = RequestMethod.POST)
    @ApiOperation(value = "addAll", notes = "添加工艺主框架生产部件及路线")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<JSONObject> addAll(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                            @RequestBody String jsonString) {
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        JSONObject mainData = jsonData.getJSONObject("craftMainFrame");
        JSONArray routeDataArr = jsonData.getJSONArray("craftMainFrameRoutes");
        JSONArray partDataArr = jsonData.getJSONArray("productionParts");

        notNull(mainData, "获取工艺主框架数据失败");
        CraftMainFrame craftMainFrame = new CraftMainFrame();
        long randomCode = SnowflakeIdUtil.generateId();
        craftMainFrame.setRandomCode(randomCode);


        craftMainFrame.setMainFrameName(mainData.getString("mainFrameName"));
        craftMainFrame.setDescription(mainData.getString("description"));
        craftMainFrame.setCraftCategoryRandomCode(mainData.getLong("craftCategoryRandomCode"));
        craftMainFrame.setCraftCategoryCode(mainData.getString("craftCategoryCode"));
        craftMainFrame.setCraftCategoryName(mainData.getString("craftCategoryName"));
        craftMainFrame.setIsDefault(mainData.getBoolean("isDefault"));
        craftMainFrame.setFrameType(mainData.getString("frameType"));
        notBlank(craftMainFrame.getMainFrameName(), "工艺主框架名称不能为空");
        notNull(craftMainFrame.getCraftCategoryRandomCode(), "工艺品类关联代码不能为空");
        notBlank(craftMainFrame.getCraftCategoryCode(), "工艺品类代码不能为空");
        notBlank(craftMainFrame.getCraftCategoryName(), "工艺品类名称不能为空");
        if(craftMainFrame.getIsDefault() != null && craftMainFrame.getIsDefault()) {
            isFalse(craftMainFrameService.isDefaultMainFrameExists(craftMainFrame.getCraftCategoryRandomCode(), null),"该品类已存在默认主框架");
        }

        String preStr = craftMainFrame.getCraftCategoryCode().substring(0, 1);
        String mainFrameCode = maxCodeService.getNextSerialNo(getModuleCode(), preStr, 2, false);
        craftMainFrame.setMainFrameCode(mainFrameCode);

        craftMainFrame.setCreateTime(new Date());
        craftMainFrame.setCreateUser(currentUser.getUserName());
        craftMainFrame.setCreateUserName(currentUser.getUserName());
        craftMainFrame.setUpdateTime(craftMainFrame.getCreateTime());
        craftMainFrame.setUpdateUser(currentUser.getUserName());
        craftMainFrame.setUpdateUserName(currentUser.getUserName());
        craftMainFrame.setStatus(DRAFT_STATUS);
        craftMainFrame.setIsInvalid(false);

        List<ProductionPart> productionParts = new ArrayList<ProductionPart>();
        for (int i = 0; i < partDataArr.size(); i++) {
            JSONObject obj = partDataArr.getJSONObject(i);
            ProductionPart part = new ProductionPart();
            long partRandomCode = SnowflakeIdUtil.generateId();
            part.setRandomCode(partRandomCode);
            part.setMainFrameRandomCode(randomCode);
            part.setMainFrameCode(craftMainFrame.getMainFrameCode());
            part.setMainFrameName(craftMainFrame.getMainFrameName());

            part.setProductionPartCode(obj.getString("productionPartCode"));
            part.setProductionPartName(obj.getString("productionPartName"));
            part.setCraftCategoryRandomCode(craftMainFrame.getCraftCategoryRandomCode());
            part.setCraftCategoryCode(craftMainFrame.getCraftCategoryCode());
            part.setCraftCategoryName(craftMainFrame.getCraftCategoryName());
            part.setRemark(obj.getString("remark"));
            part.setCustomProductionAreaCode(obj.getString("customProductionAreaCode"));
            part.setCustomProductionAreaName(obj.getString("customProductionAreaName"));


            notNull(part.getProductionPartName(), "生产部件名称不能为空");
            notNull(part.getCustomProductionAreaCode(), "定制生产区代码不能为空");
            notNull(part.getCustomProductionAreaName(), "定制生产区名称不能为空");
//            if(part.getProductionPartCode() != null && part.getProductionPartCode().length() == 2) {
//                String productionPartCode = mainFrameCode + part.getProductionPartCode();
//                part.setProductionPartCode(productionPartCode);
//            }
            notNull(part.getProductionPartCode(), "生产部件代码不能为空");
            productionParts.forEach(x -> {
                isFalse(part.getProductionPartCode().equalsIgnoreCase(x.getProductionPartCode()), "生产部件代码不能重复");
                isFalse(part.getProductionPartName().equalsIgnoreCase(x.getProductionPartName()), "生产部件名称不能重复");
            });


            part.setCreateTime(new Date());
            part.setCreateUser(currentUser.getUserName());
            part.setUpdateTime(craftMainFrame.getCreateTime());
            part.setUpdateUser(currentUser.getUserName());
            part.setStatus(DRAFT_STATUS);
            part.setIsInvalid(false);

            productionParts.add(part);
        }
        List<String> codeList = productionParts.stream().map(ProductionPart::getProductionPartCode).collect(Collectors.toList());
        isTrue(codeList.stream().distinct().count() == productionParts.size(), "生产部件代码不能重复");

        List<CraftMainFrameRoute> craftMainFrameRoutes = new ArrayList<CraftMainFrameRoute>();
        for (int i = 0; i < routeDataArr.size(); i++) {
            JSONObject obj = routeDataArr.getJSONObject(i);
            CraftMainFrameRoute route = new CraftMainFrameRoute();

            long routeRandomCode = SnowflakeIdUtil.generateId();
            route.setRandomCode(routeRandomCode);

            route.setMainFrameRandomCode(randomCode);
            route.setProductionPartCode(obj.getString("productionPartCode"));
            route.setProductionPartName(obj.getString("productionPartName"));
            route.setNextProductionPartCode(obj.getString("nextProductionPartCode"));
            route.setNextProductionPartName(obj.getString("nextProductionPartName"));

            notNull(route.getProductionPartCode(), "当前生产部件代码不能为空");
            notNull(route.getProductionPartName(), "当前生产部件名称不能为空");
            notNull(route.getNextProductionPartCode(), "后续生产部件代码不能为空");
            notNull(route.getNextProductionPartName(), "后续生产部件名称不能为空");
            isFalse(route.getProductionPartCode().equalsIgnoreCase(route.getNextProductionPartCode()), "当前生产部件代码与后续生产部件代码不能相同");
            craftMainFrameRoutes.forEach(x->{
                isFalse(x.getProductionPartCode().equalsIgnoreCase(route.getProductionPartCode())
                                && x.getNextProductionPartCode().equalsIgnoreCase(route.getNextProductionPartCode()),
                        "工艺主框架路线不能相同");
            });

            ProductionPart curPart = productionParts.stream().filter(a -> route.getProductionPartCode().equals(a.getProductionPartCode())).findFirst().orElse(null);
            notNull(curPart, "当前生产部件代码不存在" + route.getProductionPartCode());
            route.setProductionPartRandomCode(curPart.getRandomCode());

            ProductionPart nextPart = productionParts.stream().filter(a -> route.getNextProductionPartCode().equals(a.getProductionPartCode())).findFirst().orElse(null);
            notNull(curPart, "后续生产部件代码不存在" + route.getNextProductionPartCode());
            route.setNextProductionPartRandomCode(nextPart.getRandomCode());


            route.setCreateTime(new Date());
            route.setCreateUser(currentUser.getUserName());
            route.setUpdateTime(craftMainFrame.getCreateTime());
            route.setUpdateUser(currentUser.getUserName());
            route.setStatus(DRAFT_STATUS);
            route.setIsInvalid(false);

            craftMainFrameRoutes.add(route);
        }

        int ret = craftMainFrameService.insertAll(craftMainFrame, productionParts, craftMainFrameRoutes);
        if(ret != 1) {
            return Result.build(-1,"保存失败");
        }

        //返回结果
        JSONObject result = new JSONObject();
        result.put("craftMainFrame", craftMainFrame);
        result.put("productionParts", productionParts);
        result.put("craftMainFrameRoutes", craftMainFrameRoutes);
        return Result.ok(result);
    }

    @RequestMapping(value = "/updateAll", method = RequestMethod.POST)
    @ApiOperation(value = "updateAll", notes = "修改工艺主框架生产部件及路线")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<JSONObject> updateAll(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                 @RequestBody String jsonString) {
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        JSONObject mainData = jsonData.getJSONObject("craftMainFrame");
        JSONArray routeDataArr = jsonData.getJSONArray("craftMainFrameRoutes");
        JSONArray partDataArr = jsonData.getJSONArray("productionParts");

        notNull(mainData, "获取工艺主框架数据失败");
        notNull(routeDataArr, "获取工艺主框架路线图数据失败");
        notNull(partDataArr, "获取生产部件数据失败");
        CraftMainFrame craftMainFrame = new CraftMainFrame();
        craftMainFrame.setRandomCode(mainData.getLong("randomCode"));
        craftMainFrame.setMainFrameCode(mainData.getString("mainFrameCode"));
        craftMainFrame.setMainFrameName(mainData.getString("mainFrameName"));
        craftMainFrame.setDescription(mainData.getString("description"));
        craftMainFrame.setCraftCategoryRandomCode(mainData.getLong("craftCategoryRandomCode"));
        craftMainFrame.setCraftCategoryCode(mainData.getString("craftCategoryCode"));
        craftMainFrame.setCraftCategoryName(mainData.getString("craftCategoryName"));
        craftMainFrame.setIsDefault(mainData.getBoolean("isDefault"));
        craftMainFrame.setFrameType(mainData.getString("frameType"));
        notBlank(craftMainFrame.getMainFrameName(), "工艺主框架名称不能为空");
        notNull(craftMainFrame.getRandomCode(), "工艺主框架关联代码不能为空");
        notBlank(craftMainFrame.getCraftCategoryCode(), "工艺品类代码不能为空");
        notBlank(craftMainFrame.getCraftCategoryName(), "工艺品类名称不能为空");
        notNull(craftMainFrame.getCraftCategoryRandomCode(), "工艺品类关联代码不能为空");


        CraftMainFrame craftMainFrameOld = craftMainFrameService.selectByPrimaryKey(craftMainFrame.getRandomCode());
        notNull(craftMainFrameOld, "工艺主框架不存在");
//        if(craftMainFrameOld.getStatus() != null) {
//            isTrue(DRAFT_STATUS.equals(craftMainFrameOld.getStatus()), "该状态不允许修改");
//        }
        craftMainFrame.setStatus(DRAFT_STATUS);

        if(craftMainFrame.getIsDefault() != null && craftMainFrame.getIsDefault()) {
            isFalse(craftMainFrameService.isDefaultMainFrameExists(craftMainFrame.getCraftCategoryRandomCode(),
                    craftMainFrame.getRandomCode()),"该品类已存在默认主框架");
        }

        craftMainFrame.setUpdateTime(new Date());
        craftMainFrame.setUpdateUser(currentUser.getUserName());
        craftMainFrame.setUpdateUserName(currentUser.getUserName());


        List<ProductionPart> productionParts = new ArrayList<ProductionPart>();
        for (int i = 0; i < partDataArr.size(); i++) {
            JSONObject obj = partDataArr.getJSONObject(i);
            ProductionPart part = new ProductionPart();

            part.setRandomCode(obj.getLong("randomCode"));
            if (part.getRandomCode() == null) {
                long partRandomCode = SnowflakeIdUtil.generateId();
                part.setRandomCode(partRandomCode);
                part.setCreateTime(new Date());
                part.setCreateUser(currentUser.getUserName());
            }

            part.setMainFrameRandomCode(craftMainFrame.getRandomCode());
            part.setMainFrameCode(craftMainFrame.getMainFrameCode());
            part.setMainFrameName(craftMainFrame.getMainFrameName());

            part.setProductionPartCode(obj.getString("productionPartCode"));
            part.setProductionPartName(obj.getString("productionPartName"));
            part.setCraftCategoryRandomCode(craftMainFrame.getCraftCategoryRandomCode());
            part.setCraftCategoryCode(craftMainFrame.getCraftCategoryCode());
            part.setCraftCategoryName(craftMainFrame.getCraftCategoryName());
            part.setRemark(obj.getString("remark"));
            part.setCustomProductionAreaCode(obj.getString("customProductionAreaCode"));
            part.setCustomProductionAreaName(obj.getString("customProductionAreaName"));


            notNull(part.getProductionPartCode(), "生产部件代码不能为空");
            notNull(part.getProductionPartName(), "生产部件名称不能为空");
            notNull(part.getCustomProductionAreaCode(), "定制生产区代码不能为空");
            notNull(part.getCustomProductionAreaName(), "定制生产区名称不能为空");

            productionParts.forEach(x -> {
                isFalse(part.getProductionPartCode().equalsIgnoreCase(x.getProductionPartCode()), "生产部件代码不能重复");
                isFalse(part.getProductionPartName().equalsIgnoreCase(x.getProductionPartName()), "生产部件名称不能重复");
            });

            part.setUpdateTime(craftMainFrame.getUpdateTime());
            part.setUpdateUser(craftMainFrame.getUpdateUser());
            productionParts.add(part);
        }
        List<String> codeList = productionParts.stream().map(ProductionPart::getProductionPartCode).collect(Collectors.toList());
        isTrue(codeList.stream().distinct().count() == productionParts.size(), "生产部件代码不能重复");

        List<CraftMainFrameRoute> craftMainFrameRoutes = new ArrayList<CraftMainFrameRoute>();
        for (int i = 0; i < routeDataArr.size(); i++) {
            JSONObject obj = routeDataArr.getJSONObject(i);
            CraftMainFrameRoute route = new CraftMainFrameRoute();

            route.setRandomCode(obj.getLong("randomCode"));
            if(route.getRandomCode() == null) {
                long routeRandomCode = SnowflakeIdUtil.generateId();
                route.setRandomCode(routeRandomCode);
                route.setCreateTime(craftMainFrame.getUpdateTime());
                route.setCreateUser(craftMainFrame.getUpdateUser());
            }

            route.setMainFrameRandomCode(craftMainFrame.getRandomCode());
            route.setProductionPartCode(obj.getString("productionPartCode"));
            route.setProductionPartName(obj.getString("productionPartName"));
            route.setNextProductionPartCode(obj.getString("nextProductionPartCode"));
            route.setNextProductionPartName(obj.getString("nextProductionPartName"));

            notNull(route.getProductionPartCode(), "当前生产部件代码不能为空");
            notNull(route.getProductionPartName(), "当前生产部件名称不能为空");
            notNull(route.getNextProductionPartCode(), "后续生产部件代码不能为空");
            notNull(route.getNextProductionPartName(), "后续生产部件名称不能为空");
            isFalse(route.getProductionPartCode().equalsIgnoreCase(route.getNextProductionPartCode()), "当前生产部件代码与后续生产部件代码不能相同");
            craftMainFrameRoutes.forEach(x->{
                isFalse(x.getProductionPartCode().equalsIgnoreCase(route.getProductionPartCode())
                        && x.getNextProductionPartCode().equalsIgnoreCase(route.getNextProductionPartCode()),
                        "工艺主框架路线不能相同");
            });


            ProductionPart curPart = productionParts.stream().filter(a -> route.getProductionPartCode().equals(a.getProductionPartCode())).findFirst().orElse(null);
            notNull(curPart, "当前生产部件代码不存在" + route.getProductionPartCode());
            route.setProductionPartRandomCode(curPart.getRandomCode());

            ProductionPart nextPart = productionParts.stream().filter(a -> route.getNextProductionPartCode().equals(a.getProductionPartCode())).findFirst().orElse(null);
            notNull(nextPart, "后续生产部件代码不存在" + route.getNextProductionPartCode());
            route.setNextProductionPartRandomCode(nextPart.getRandomCode());

            route.setUpdateTime(craftMainFrame.getUpdateTime());
            route.setUpdateUser(craftMainFrame.getUpdateUser());
            craftMainFrameRoutes.add(route);
        }


        int ret = craftMainFrameService.updateAll(craftMainFrame, productionParts, craftMainFrameRoutes);
        if(ret != 1) {
            return Result.build(-1,"保存失败");
        } else {
            //异常执行
            if(!craftMainFrameOld.getMainFrameName().equals(craftMainFrame.getMainFrameName())) {
                craftMainFrameService.updateRelateMainFrameInfo(craftMainFrame.getMainFrameCode());
            }

        }

        //返回结果
        CraftMainFrame craftMainFrameSave = craftMainFrameService.selectByPrimaryKey(craftMainFrame.getRandomCode());
        List<ProductionPart> productionPartSave = productionPartService.getByMainFrameRandomCode(craftMainFrame.getRandomCode());
        List<CraftMainFrameRoute> mainFrameRouteSave = craftMainFrameRouteService.getByMainFrameRandomCode(craftMainFrame.getRandomCode());

        JSONObject result = new JSONObject();
        result.put("craftMainFrame", craftMainFrameSave);
        result.put("productionParts", productionPartSave);
        result.put("craftMainFrameRoutes", mainFrameRouteSave);
        return Result.ok(result);
    }



    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "修改工艺主框架")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestBody CraftMainFrame craftMainFrame) {
        notNull(craftMainFrame.getRandomCode(), "工艺主框架关联代码不能为空");
        notNull(craftMainFrame.getCraftCategoryRandomCode(), "工艺品类关联代码不能为空");
        if(craftMainFrame.getIsDefault() != null && craftMainFrame.getIsDefault()) {
            isFalse(craftMainFrameService.isDefaultMainFrameExists(craftMainFrame.getCraftCategoryRandomCode(),
                    craftMainFrame.getRandomCode()),"该品类已存在默认主框架");
        }
        craftMainFrame.setId(null);
        craftMainFrame.setStatus(null);
        craftMainFrame.setUpdateUser(currentUser.getUserName());
        craftMainFrame.setUpdateUserName(currentUser.getUserName());
        craftMainFrame.setUpdateTime(new Date());
        return Result.ok(craftMainFrameService.updateByPrimaryKeySelective(craftMainFrame));
    }


    @RequestMapping(value = "/publish", method = RequestMethod.PUT)
    @ApiOperation(value = "publish", notes = "发布工艺主框架")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<CraftMainFrame> publish(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                   @RequestParam(name = "randomCode", required = true) String randomCode) {
        notNull(randomCode, "关联代码不能为空");
        Long randomId = Long.parseLong(randomCode);
        CraftMainFrame craftMainFrameOld = craftMainFrameService.selectByPrimaryKey(randomId);
        notNull(craftMainFrameOld, "未找到工艺主框架");
        Integer status = craftMainFrameOld.getStatus();
        if(status == null) {
            status = DRAFT_STATUS;
        }
        isTrue(status.equals(DRAFT_STATUS), "当前状态不允许发布");
        CraftMainFrame craftMainFrame = new CraftMainFrame();
        craftMainFrame.setRandomCode(randomId);
        craftMainFrame.setStatus(PUBLISHED_STATUS);
        craftMainFrame.setAuditTime(new Date());
        craftMainFrame.setAuditUser(currentUser.getUserName());
        craftMainFrame.setAuditUserName(currentUser.getUserName());
        craftMainFrame.setUpdateTime(new Date());
        craftMainFrame.setUpdateUser(currentUser.getUserName());
        craftMainFrame.setUpdateUserName(currentUser.getUserName());
        int ret = craftMainFrameService.updateByPrimaryKeySelective(craftMainFrame);
        if(ret != 1) {
            return Result.build(-1,"保存失败");
        }

        CraftMainFrame craftMainFrameSave = craftMainFrameService.selectByPrimaryKey(craftMainFrame.getRandomCode());
        return Result.ok(craftMainFrameSave);
    }


    @RequestMapping(value = "/cancel", method = RequestMethod.PUT)
    @ApiOperation(value = "cancel", notes = "作废工艺主框架")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<CraftMainFrame> cancel(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @RequestParam(name = "randomCode", required = true) String randomCode) {
        notNull(randomCode, "关联代码不能为空");
        Long randomId = Long.parseLong(randomCode);
        CraftMainFrame craftMainFrameOld = craftMainFrameService.selectByPrimaryKey(randomId);
        notNull(craftMainFrameOld, "未找到工艺主框架");
        Integer status = craftMainFrameOld.getStatus();
        if(status == null) {
            status = DRAFT_STATUS;
        }
        isTrue(!status.equals(INVALID_STATUS), "不允许重复作废");
        CraftMainFrame craftMainFrame = new CraftMainFrame();
        craftMainFrame.setRandomCode(randomId);
        craftMainFrame.setStatus(INVALID_STATUS);
        craftMainFrame.setUpdateTime(new Date());
        craftMainFrame.setUpdateUser(currentUser.getUserName());
        craftMainFrame.setUpdateUserName(currentUser.getUserName());
        int ret = craftMainFrameService.updateByPrimaryKeySelective(craftMainFrame);
        if(ret != 1) {
            return Result.build(-1,"保存失败");
        } else {
            List<ProductionPart> productionParts = productionPartService.getByMainFrameRandomCode(randomId);
            for(ProductionPart part: productionParts) {
                part.setStatus(INVALID_STATUS);
                part.setUpdateTime(craftMainFrame.getUpdateTime());
                part.setUpdateUser(currentUser.getUserName());
                productionPartService.updateByPrimaryKeySelective(part);
            }
            List<CraftMainFrameRoute> craftMainFrameRoutes = craftMainFrameRouteService.getByMainFrameRandomCode(randomId);
            for(CraftMainFrameRoute route: craftMainFrameRoutes) {
                route.setStatus(INVALID_STATUS);
                route.setUpdateTime(craftMainFrame.getUpdateTime());
                route.setUpdateUser(currentUser.getUserName());
                craftMainFrameRouteService.updateByPrimaryKeySelective(route);
            }

        }
        CraftMainFrame craftMainFrameSave = craftMainFrameService.selectByPrimaryKey(randomId);
        return Result.ok(craftMainFrameSave);

    }


    @RequestMapping(value = "/delete/{randomCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除工艺主框架")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("randomCodes") String randomCodes) {
        notBlank(randomCodes, "工艺主框架关联代码不能为空");
        String[] split = randomCodes.split(",");

        int ret = 0;
        CraftMainFrame craftMainFrame = new CraftMainFrame();
        craftMainFrame.setIsInvalid(true);
        craftMainFrame.setStatus(IN_VALID);
        craftMainFrame.setUpdateTime(new Date());
        craftMainFrame.setUpdateUser(currentUser.getUserName());
        craftMainFrame.setUpdateUserName(currentUser.getUserName());
        for(String code: split) {
            craftMainFrame.setRandomCode(Long.parseLong(code));
            ret += craftMainFrameService.updateByPrimaryKeySelective(craftMainFrame);

            List<ProductionPart> productionParts = productionPartService.getByMainFrameRandomCode(craftMainFrame.getRandomCode());
            for(ProductionPart part: productionParts) {
                part.setIsInvalid(true);
                part.setStatus(IN_VALID);
                part.setUpdateTime(craftMainFrame.getUpdateTime());
                part.setUpdateUser(currentUser.getUserName());
                productionPartService.updateByPrimaryKeySelective(part);
            }
            List<CraftMainFrameRoute> craftMainFrameRoutes = craftMainFrameRouteService.getByMainFrameRandomCode(craftMainFrame.getRandomCode());
            for(CraftMainFrameRoute route: craftMainFrameRoutes) {
                route.setIsInvalid(true);
                route.setStatus(IN_VALID);
                route.setUpdateTime(craftMainFrame.getUpdateTime());
                route.setUpdateUser(currentUser.getUserName());
                craftMainFrameRouteService.updateByPrimaryKeySelective(route);
            }
        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "export", notes = "导出工艺主框架")
    @Authentication(auth = Authentication.AuthType.EXPORT, required = false)
    public Result export(HttpServletResponse response,
                         @RequestParam(name = "keywords", required = false) String keywords,
                         @RequestParam(name = "beginDate", required = false) Date beginDate,
                         @RequestParam(name = "endDate", required = false) Date endDate) {

        List<CraftMainFrame> craftMainFrames = craftMainFrameService.getByCondition(keywords, beginDate, endDate);
        isFalse(craftMainFrames.isEmpty(), "无数据导出");
        List<CraftMainFrameExportVo> lst = craftMainFrameService.getCraftMainFrameExportVos(craftMainFrames);

        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名
            writer.addHeaderAlias("lineNo", "行号");
            writer.addHeaderAlias("craftCategoryName", "工艺品类");
            writer.addHeaderAlias("mainFrameCode", "工艺主框架编码");
            writer.addHeaderAlias("mainFrameName", "工艺主框架名称");
            writer.addHeaderAlias("description", "描述");
            writer.addHeaderAlias("isDefault", "默认");
            writer.addHeaderAlias("statusName", "状态");
            writer.addHeaderAlias("updateUser", "更新人");
            writer.addHeaderAlias("updateTime", "更新时间");
            writer.write(lst, true);

            String fileName = URLEncoder.encode("工艺主框架清单.xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            LOGGER.error("craftMainFrames export fails", e);
        } finally {
            writer.close();
            if (null != out) {
                IoUtil.close(out);
            }
        }
        return null;
    }


    @Override
    public String getModuleCode() {
        return "craft-main-frame";
    }


}
