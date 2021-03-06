package com.ylzs.controller.thinkstyle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.plm.PICustomOrder;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.thinkstyle.ThinkStyleCraft;
import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.entity.thinkstyle.ThinkStyleProcessRule;
import com.ylzs.entity.thinkstyle.ThinkStyleWarehouse;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.custom.ICustomStyleCraftCourseService;
import com.ylzs.service.interfaceOutput.IOperationPathService;
import com.ylzs.service.plm.IPICustomOrderService;
import com.ylzs.service.plm.IProductModelMasterDataService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import com.ylzs.service.thinkstyle.*;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.thinkstyle.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.ylzs.common.constant.BusinessConstants.Status.*;
import static com.ylzs.common.util.Assert.*;
import static com.ylzs.common.util.StringUtils.RightTrimHide;

/**
 * @author ???lyq
 * @description??????????????????
 * @date ???2020-03-06 11:00
 */
@Api(tags = "???????????????")
@RestController
@RequestMapping(value = "/think-style")
public class ThinkStyleController implements IModuleInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThinkStyleController.class);

    @Resource
    IThinkStyleWarehouseService thinkStyleWarehouseService;

    @Resource
    IThinkStylePartService thinkStylePartService;

    @Resource
    IThinkStyleCraftService thinkStyleCraftService;

    @Resource
    IThinkStyleFabricService thinkStyleFabricService;

    @Resource
    IThinkStyleProcessRuleService thinkStyleProcessRuleService;

    @Resource
    IThinkStyleCraftHistoryService thinkStyleCraftHistoryService;

    @Resource
    IDictionaryService dictionaryService;

    @Resource
    SewingCraftWarehouseService sewingCraftWarehouseService;

    @Resource
    ThreadPoolTaskExecutor taskExecutor;

    @Resource
    IOperationPathService operationPathService;

    @Resource
    IProductModelMasterDataService productModelMasterDataService;

    @Resource
    ICraftCategoryService craftCategoryService;

    @Resource
    IPICustomOrderService ipiCustomOrderService;

    @Resource
    ICustomStyleCraftCourseService customStyleCraftCourseService;



    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//???????????????????????? false??????????????? true????????????
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/getDropDownData", method = RequestMethod.GET)
    @ApiOperation(value = "getDropDownData", notes = "???????????????????????????")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<JSONObject> getDropDownData() {
        List<CraftCategoryVo> craftCategoryVos = thinkStyleWarehouseService.getCraftCategoryVos();
        List<DictionaryVo> dictionaryVos = dictionaryService.getDictoryAll("ClothesCategory");

        //????????????
        JSONObject result = new JSONObject();
        result.put("craftCategorys", craftCategoryVos);
        result.put("clothesCategorys", dictionaryVos);

        return Result.ok(result);
    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "???????????????????????????")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<ThinkStyleWarehouseListVo>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                          @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                          @RequestParam(name = "keywords", required = false) String keywords,
                                                          @RequestParam(name = "craftCategoryCodes", required = false) String craftCategoryCodes,
                                                          @RequestParam(name = "clothesCategoryCodes", required = false) String clothesCategoryCodes,
                                                          @RequestParam(name = "updateDateStart", required = false) Date updateDateStart,
                                                          @RequestParam(name = "updateDateStop", required = false) Date updateDateStop,
                                                          @RequestParam(name = "status", required = false) Integer status) {
        //0 ?????? 1?????? 2????????? 3?????????
        Boolean isInvalid = null;
        Integer busStatus = null;
        if (status != null) {
            if (status.equals(1)) {
                busStatus = DRAFT_STATUS;
            } else if (status.equals(2)) {
                busStatus = PUBLISHED_STATUS;
            } else if (status.equals(3)) {
                isInvalid = true;
            }
        }
        String[] craftCategoryCodeArr = null;
        if (craftCategoryCodes != null && !craftCategoryCodes.isEmpty()) {
            craftCategoryCodeArr = craftCategoryCodes.split(",");
        }
        String[] clothesCategoryCodeArr = null;
        if (clothesCategoryCodes != null && !clothesCategoryCodes.isEmpty()) {
            clothesCategoryCodeArr = clothesCategoryCodes.split(",");
        }

        PageHelper.startPage(page, rows);
        List<ThinkStyleWarehouse> thinkStyleWarehouses = thinkStyleWarehouseService.selectAllThinkStyle(keywords, craftCategoryCodeArr, clothesCategoryCodeArr, updateDateStart, updateDateStop, busStatus, isInvalid);
        PageInfo<ThinkStyleWarehouse> pageInfo = new PageInfo<>(thinkStyleWarehouses);
        List<ThinkStyleWarehouseListVo> thinkStyleWarehouseListVos = thinkStyleWarehouses.stream().map(thinkStyleWarehouseService::getThinkStyleWarehouseListVo).collect(Collectors.toList());
        return Result.ok(thinkStyleWarehouseListVos, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getCraftHistory/{partRandomCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getCraftHistory", notes = "???????????????????????????????????????")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<ThinkStyleCraftHistoryVo>> getPartHistory(@PathVariable("partRandomCode") String partRandomCode) {
        notBlank(partRandomCode, "????????????????????????????????????????????????");
        Long partRandomId = Long.parseLong(partRandomCode);
        return Result.ok(thinkStyleCraftHistoryService.getThinkStyleCraftHistoryVos(partRandomId));
    }


    @RequestMapping(value = "/getPartDetail/{partRandomCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getPartDetail", notes = "??????????????????????????????????????????")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<JSONObject> getPartDetail(@PathVariable("partRandomCode") String partRandomCode) {
        notBlank(partRandomCode, "????????????????????????????????????????????????");
        Long partRandomId = Long.parseLong(partRandomCode);
        QueryWrapper<ThinkStylePart> partWrapper = new QueryWrapper<>();
        partWrapper.lambda().eq(ThinkStylePart::getRandomCode, partRandomCode);
        ThinkStylePart part = thinkStylePartService.getOne(partWrapper);
        notNull(part, "????????????????????????");

        QueryWrapper<ThinkStyleWarehouse> styleWrapper = new QueryWrapper<>();
        styleWrapper.lambda().eq(ThinkStyleWarehouse::getRandomCode, part.getStyleRandomCode());
        ThinkStyleWarehouse style = thinkStyleWarehouseService.getOne(styleWrapper);
        notNull(style, "??????????????????");

        List<ThinkStyleCraftVo> thinkStyleCraftVos = new ArrayList<>();
        List<ThinkStyleProcessRuleVo> thinkStyleProcessRuleVos = new ArrayList<>();
        thinkStyleWarehouseService.getThinkStylePartInfo(style, part, thinkStyleCraftVos, thinkStyleProcessRuleVos);

        BigDecimal standardPrice = new BigDecimal(0);
        BigDecimal standardTime = new BigDecimal(0);
        //MathContext mathContext = new MathContext(3, RoundingMode.HALF_EVEN);
        for (ThinkStyleCraftVo vo : thinkStyleCraftVos) {
            if (vo.getStandardPrice() != null) {
                standardPrice = standardPrice.add(vo.getStandardPrice());
            }
            if (vo.getStandardTime() != null) {
                standardTime = standardTime.add(vo.getStandardTime());
            }
        }


        //????????????
        JSONObject result = new JSONObject();
        result.put("crafts", thinkStyleCraftVos);
        result.put("rules", thinkStyleProcessRuleVos);
        result.put("standardPrice", standardPrice);
        result.put("standardTime", standardTime);

        return Result.ok(result);
    }

    @RequestMapping(value = "/publish", method = RequestMethod.PUT)
    @ApiOperation(value = "publish", notes = "???????????????")
    @Authentication(auth = Authentication.AuthType.COMMIT, required = true)
    public Result<ThinkStyleWarehouse> publish(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = true) UserContext currentUser,
                                               @RequestParam(name = "styleRandomCode", required = true) String styleRandomCode) {
        Long randomId = Long.parseLong(styleRandomCode);
        QueryWrapper<ThinkStyleWarehouse> styleWrapper = new QueryWrapper<>();
        styleWrapper.lambda().eq(ThinkStyleWarehouse::getRandomCode, randomId);
        ThinkStyleWarehouse style = thinkStyleWarehouseService.getOne(styleWrapper);
        notNull(style, "????????????????????????");
        Integer status = DRAFT_STATUS;
        if (style.getStatus() != null) {
            status = style.getStatus();
        }
        isTrue(status.equals(DRAFT_STATUS), "????????????????????????");
        notNull(style.getStandardTime(), "????????????????????????");
        notNull(style.getStandardPrice(), "????????????????????????");
//        isTrue(style.getVersion() != null && !style.getVersion().isEmpty(), "??????????????????");
//        isTrue(style.getVersionDesc() != null && !style.getVersionDesc().isEmpty(), "?????????????????????");


        String checkRet = thinkStyleWarehouseService.checkPublish(style);
        isTrue(checkRet == null, checkRet);

        //????????????
        style.setUpdateUser(currentUser.getUserName());
        style.setUpdateUserName(currentUser.getUserName());
        style.setUpdateTime(new Date());
        style.setPublishUser(currentUser.getUserName());
        style.setPublishUserName(currentUser.getUserName());
        style.setPublishTime(style.getUpdateTime());
        style.setStatus(PUBLISHED_STATUS);
        boolean ret = thinkStyleWarehouseService.update(style, styleWrapper);
        if (ret) {
            taskExecutor.execute(() -> {
                thinkStyleWarehouseService.sendThinkStylePublishInfo(randomId);
                List<PICustomOrder> piCustomOrders = ipiCustomOrderService.getCustomOrderInvalid(style.getStyleCode());
                for(PICustomOrder customOrder: piCustomOrders) {
                    customStyleCraftCourseService.createCustomStyleCratCouresData(customOrder);
                }
            });

        }
        return Result.ok(thinkStyleWarehouseService.getThinkStyleWarehouse(randomId));
    }

    @RequestMapping(value = "/delete/{styleRandomCode}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "???????????????")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<ThinkStyleWarehouse> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = true) UserContext currentUser,
                                              @PathVariable("styleRandomCode") String styleRandomCode) {
        notBlank(styleRandomCode, "?????????????????????????????????");
        Long randomId = Long.parseLong(styleRandomCode);
        QueryWrapper<ThinkStyleWarehouse> styleQuery = new QueryWrapper<>();
        styleQuery.lambda().eq(ThinkStyleWarehouse::getRandomCode, randomId);
        ThinkStyleWarehouse style = thinkStyleWarehouseService.getOne(styleQuery);
        notNull(style, "????????????????????????");
        isTrue(style.getStatus() == null || style.getStatus().equals(DRAFT_STATUS), "???????????????????????????");
        style.setStatus(IN_VALID);
        style.setUpdateTime(new Date());
        style.setUpdateUser(currentUser.getUserName());
        style.setUpdateUserName(currentUser.getUserName());
        boolean ret = thinkStyleWarehouseService.update(style, styleQuery);
        return Result.ok(thinkStyleWarehouseService.getThinkStyleWarehouse(randomId));
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "update", notes = "?????????????????????")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<ThinkStyleWarehouse> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = true) UserContext currentUser,
                                              @RequestBody String jsonString
    ) {
        JSONObject rootJsonData = JSONObject.parseObject(jsonString);
        notBlank(rootJsonData.getString("styleRandomCode"), "?????????????????????????????????");

        ThinkStyleWarehouse style = new ThinkStyleWarehouse();
        style.setRandomCode(Long.valueOf(rootJsonData.getString("styleRandomCode")));
        style.setCraftVersion(rootJsonData.getString("craftVersion"));
        style.setVersionDesc(rootJsonData.getString("versionDesc"));
        style.setThinkStyleDesc(rootJsonData.getString("thinkStyleDesc"));
        style.setUpdateTime(new Date());
        style.setUpdateUser(currentUser.getUserName());
        style.setUpdateUserName(currentUser.getUserName());
        style.setStatus(DRAFT_STATUS);

        JSONObject jsonData = rootJsonData.getJSONObject("partInfos");

        List<ThinkStylePart> partList = null;
        if (jsonData != null) {
            JSONArray jsonPartArr = jsonData.getJSONArray("parts");
            if (jsonPartArr != null) {
                partList = new ArrayList<>();
                for (int i = 0; i < jsonPartArr.size(); i++) {
                    JSONObject jsonPart = jsonPartArr.getJSONObject(i);

                    //????????????????????????????????????????????????????????????
                    JSONArray jsonPartChild = jsonPart.getJSONArray("children");
                    if (jsonPartChild != null && jsonPartChild.size() > 0) {
                        jsonPartArr.addAll(i + 1, jsonPartChild);
                    }

                    ThinkStylePart part = new ThinkStylePart();
                    part.setStyleRandomCode(style.getRandomCode());
                    part.setRandomCode(jsonPart.getLong("randomCode"));
                    part.setIsSpecial(jsonPart.getBoolean("isSpecial"));
                    part.setOrderNum(jsonPart.getInteger("orderNum"));
                    part.setUpdateTime(style.getUpdateTime());
                    part.setUpdateUser(style.getUpdateUser());

                    if (jsonPart.getBoolean("isSpecial") == null || jsonPart.getBoolean("isSpecial").equals(false)) {
                        partList.add(part);
                        continue;
                    }


                    JSONArray jsonCraftArr = jsonPart.getJSONArray("crafts");
                    if (jsonCraftArr != null) {
                        List<ThinkStyleCraft> craftList = new ArrayList<>();
                        for (int j = 0; j < jsonCraftArr.size(); j++) {
                            ThinkStyleCraft craft = new ThinkStyleCraft();
                            JSONObject jsonCraft = jsonCraftArr.getJSONObject(j);
                            craft.setStyleRandomCode(style.getRandomCode());
                            craft.setPartRandomCode(part.getRandomCode());
                            craft.setRandomCode(jsonCraft.getLong("randomCode"));
                            craft.setIsNew(jsonCraft.getBoolean("isNew"));
                            notNull(jsonCraft.getLong("craftRandomCode"), "??????????????????????????????");
                            craft.setCraftRandomCode(jsonCraft.getLong("craftRandomCode"));
                            notNull(jsonCraft.getString("craftCode"), "????????????????????????");
                            craft.setCraftCode(jsonCraft.getString("craftCode"));
                            craft.setCraftFlowNum(jsonCraft.getInteger("craftFlowNum"));
                            craft.setCraftName(jsonCraft.getString("craftName"));
                            craft.setCraftDesc(jsonCraft.getString("craftDesc"));
//                            craft.setStandardTime(jsonCraft.getBigDecimal("standardTime"));
//                            craft.setStandardPrice(jsonCraft.getBigDecimal("standardPrice"));
                            isTrue(craftList.stream().filter(x -> craft.getCraftCode().equals(x.getCraftCode()))
                                            .findFirst().orElse(null) == null,
                                    "????????????????????????");

                            craftList.add(craft);
                        }
                        part.setCraftList(craftList);
                    }

                    String ruleLineStr;
                    Set<String> ruleLineSet = new HashSet<>();
                    JSONArray jsonRuleArr = jsonPart.getJSONArray("rules");
                    if (jsonRuleArr != null) {
                        List<ThinkStyleProcessRule> ruleList = new ArrayList<>();
                        for (int j = 0; j < jsonRuleArr.size(); j++) {
                            ThinkStyleProcessRule rule = new ThinkStyleProcessRule();
                            JSONObject jsonRule = jsonRuleArr.getJSONObject(j);
                            rule.setStyleRandomCode(style.getRandomCode());
                            rule.setPartRandomCode(part.getRandomCode());
                            rule.setRandomCode(jsonRule.getLong("randomCode"));

                            rule.setProcessType(jsonRule.getByte("processType"));
                            rule.setSourceCraftRandomCode(jsonRule.getString("sourceCraftRandomCode"));
                            rule.setSourceCraftCode(jsonRule.getString("sourceCraftCode"));
                            rule.setSourceCraftName(jsonRule.getString("sourceCraftName"));
                            rule.setActionCraftRandomCode(jsonRule.getString("actionCraftRandomCode"));
                            rule.setActionCraftCode(jsonRule.getString("actionCraftCode"));
                            rule.setActionCraftName(jsonRule.getString("actionCraftName"));
                            try {
                                if (jsonRule.getInteger("processingSortNum") != null) {
                                    rule.setProcessingSortNum(jsonRule.getInteger("processingSortNum"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            notNull(rule.getProcessType(), "??????????????????????????????");
                            isTrue(rule.getSourceCraftCode() != null ||
                                    rule.getActionCraftCode() != null, "??????????????????????????????");
                            if (StringUtils.isNotEmpty(rule.getSourceCraftCode())) {
                                //??????????????????????????????????????????RandomCode
                                if (StringUtils.isEmpty(rule.getSourceCraftRandomCode())) {
                                    String[] split = rule.getSourceCraftCode().split("#");
                                    String randomCodes = "";
                                    for (String code : split) {
                                        QueryWrapper<SewingCraftWarehouse> craftWarehouseQueryWrapper = new QueryWrapper<>();
                                        craftWarehouseQueryWrapper.lambda().select(SewingCraftWarehouse::getRandomCode)
                                                .eq(SewingCraftWarehouse::getCraftCode, code)
                                                .ne(SewingCraftWarehouse::getStatus, IN_VALID);
                                        SewingCraftWarehouse sewingCraftWarehouse = sewingCraftWarehouseService.getOne(craftWarehouseQueryWrapper);
                                        notNull(sewingCraftWarehouse, "?????????????????????" + code);
                                        if (randomCodes != "") {
                                            randomCodes += "#";
                                        }
                                        randomCodes += sewingCraftWarehouse.getRandomCode().toString();
                                    }
                                    if (randomCodes != "") {
                                        rule.setSourceCraftRandomCode(randomCodes);
                                    }
                                }

                            }

                            if (StringUtils.isNotEmpty(rule.getActionCraftCode())) {
                                //??????????????????????????????????????????RandomCode
                                if (StringUtils.isEmpty(rule.getActionCraftRandomCode())) {
                                    String[] split = rule.getActionCraftCode().split("#");
                                    String randomCodes = "";
                                    for (String code : split) {
                                        QueryWrapper<SewingCraftWarehouse> craftWarehouseQueryWrapper = new QueryWrapper<>();
                                        craftWarehouseQueryWrapper.lambda().select(SewingCraftWarehouse::getRandomCode)
                                                .eq(SewingCraftWarehouse::getCraftCode, code)
                                                .ne(SewingCraftWarehouse::getStatus, IN_VALID);
                                        SewingCraftWarehouse sewingCraftWarehouse = sewingCraftWarehouseService.getOne(craftWarehouseQueryWrapper);
                                        notNull(sewingCraftWarehouse, "?????????????????????" + code);
                                        if (randomCodes != "") {
                                            randomCodes += "#";
                                        }
                                        randomCodes += sewingCraftWarehouse.getRandomCode().toString();
                                    }
                                    if (randomCodes != "") {
                                        rule.setActionCraftRandomCode(randomCodes);
                                    }
                                }

                            }

                            ruleLineStr = (rule.getSourceCraftCode() == null ? "" : rule.getSourceCraftCode()) +
                                    rule.getProcessType().toString() +
                                    (rule.getActionCraftCode() == null ? "" : rule.getActionCraftCode());
                            isFalse(ruleLineSet.contains(ruleLineStr), "??????????????????????????????");
                            ruleLineSet.add(ruleLineStr);

                            ruleList.add(rule);
                        }
                        part.setRuleList(ruleList);
                    }
                    partList.add(part);
                }
                style.setPartList(partList);
            }
        }

        Integer ret = thinkStyleWarehouseService.updateThinkStyleWarehouse(style);
        return Result.ok(thinkStyleWarehouseService.getThinkStyleWarehouse(style.getRandomCode()));
    }


    @RequestMapping(value = "/getOne/{randomCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "???????????????????????????")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<JSONObject> getOne(@PathVariable("randomCode") String randomCode) {
        notBlank(randomCode, "????????????????????????????????????");
        Long randomId = Long.valueOf(randomCode);

        //???????????????
        QueryWrapper<ThinkStyleWarehouse> styleWrapper = new QueryWrapper<>();
        styleWrapper.lambda().eq(ThinkStyleWarehouse::getRandomCode, randomId);
        ThinkStyleWarehouse thinkStyleWarehouse = thinkStyleWarehouseService.getOne(styleWrapper);
        notNull(thinkStyleWarehouse, "????????????????????????");
        ThinkStyleWarehouseViewVo thinkStyleWarehouseViewVo = thinkStyleWarehouseService.getThinkStyleWarehouseViewVo(thinkStyleWarehouse);

        //????????????????????? ???????????????????????????????????????????????????
        List<ThinkStylePartVo> thinkStylePartVos = thinkStylePartService.selectThinkStylePartVo(randomId, thinkStyleWarehouse.getClothesCategoryCode());
        //???????????????
        List<ThinkStyleFabricVo> thinkStyleFabricVos = thinkStyleFabricService.getThinkStyleFabricVos(randomId);
        //????????????
        //List<ThinkStyleHistoryVo> thinkStyleHistoryVos = thinkStyleWarehouseService.getThinkStyleHistoryVos(randomId);
        String craftCategoryCode = craftCategoryService.getCraftCategoryCode(thinkStyleWarehouse.getClothesCategoryCode());

        //????????????
        JSONObject result = new JSONObject();
        result.put("baseInfo", thinkStyleWarehouseViewVo);
        result.put("designPartInfo", thinkStylePartVos);
        result.put("materialInfo", thinkStyleFabricVos);
        result.put("craftCategoryCode", craftCategoryCode);
        //result.put("historyVersion", thinkStyleHistoryVos);
        return Result.ok(result);
    }

    @RequestMapping(value = "/getCustomFlowNum", method = RequestMethod.GET)
    @ApiOperation(value = "getCustomFlowNum", notes = "??????????????????")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<CustomFlowNumVo>> getCustomFlowNum(@RequestParam(name = "clothesCategoryCode", required = true) String clothesCategoryCode,
                                         @RequestParam(name = "craftCodes", required = true) String craftCodes) {
        notBlank(clothesCategoryCode, "??????????????????????????????");
        notBlank(craftCodes, "????????????????????????");
        String[] split = craftCodes.split(",");
        List<CustomFlowNumVo> customFlowNumVos = thinkStyleCraftService.getCustomFlowNum(clothesCategoryCode, split);
        return Result.ok(customFlowNumVos);
    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation(value = "test", notes = "????????????")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<JSONObject> test() {
//        thinkStyleWarehouseService.addOrUpdateProductModelDataDao("ZF0199");
//        List<String> codes = productModelMasterDataService.getAllStyleCode();
//        for(String code: codes) {
//            try {
//                System.out.println(code);
//                thinkStyleWarehouseService.addOrUpdateProductModelDataDao(code);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        String str = RightTrimHide("abcd");
        System.out.println(str);

        JSONObject result = new JSONObject();
        return Result.ok(result);

    }


    @Override
    public String getModuleCode() {
        return "think-style";
    }
}
