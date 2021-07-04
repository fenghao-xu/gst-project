package com.ylzs.controller.partCombCraft;

import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.util.pageHelp.PageUtils;
import com.ylzs.entity.auth.CappUserClothingCategory;
import com.ylzs.entity.auth.resp.CappUserDataAuthResp;
import com.ylzs.entity.partCombCraft.PartCombCraft;
import com.ylzs.entity.partCombCraft.req.PartCombCraftReq;
import com.ylzs.entity.partCombCraft.req.QueryPartCombCraftReq;
import com.ylzs.entity.partCombCraft.req.UpdatePartCombCraftReq;
import com.ylzs.entity.partCombCraft.resp.PartCombCraftAllDataResp;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.partCombCraft.IPartCombCraftService;
import com.ylzs.service.system.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.ylzs.common.util.Assert.notBlank;

/**
 * @author weikang
 * @Description
 * @Date 2020/3/12
 */
@Api(tags = "部件组合工艺")
@RestController
@RequestMapping("/partCombCraft")
public class PartCombCraftController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartCombCraftController.class);

    @Resource
    private IPartCombCraftService combCraftService;

    @Resource
    private IDictionaryService dictionaryService;

    @Resource
    private IUserService userService;

    @ApiOperation(value = "addPartCombCraft", notes = "新增部件组合工艺数据", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/addPartCombCraft", method = RequestMethod.POST)
    public Result addPartCombCraft(@RequestBody PartCombCraftReq req) throws Exception {
        return combCraftService.addPartCombCraft(req);
    }

    @ApiOperation(value = "updatePartCombCraft", notes = "修改部件组合工艺状态", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/updatePartCombCraft", method = RequestMethod.POST)
    public Result updatePartCombCraft(@RequestBody UpdatePartCombCraftReq req) throws Exception {
        //删除 草稿
        if (req.getOperateType().intValue() == BusinessConstants.CommonConstant.ONE) {
            if (req.getRandomCode().longValue() == 0L) {
                LOGGER.info("无部件组合工艺草稿【{}】");
                return Result.ok();
            }
        } else {
            if (req.getRandomCode() == null)
                return Result.build(BusinessConstants.CommonConstant.ZERO, "部件组合工艺randomCode不能为空");
        }
        if(StringUtils.isBlank(req.getUserCode())){
            return Result.build(BusinessConstants.CommonConstant.ZERO, "用户信息为空，不能操作");
        }
        return combCraftService.updatePartCombCraft(req);
    }

    @ApiOperation(value = "selectPartCombCraftChecklist", notes = "获取部件组合工艺数据清单", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/selectPartCombCraftChecklist", method = RequestMethod.POST)
    public Result selectPartCombCraftChecklist(@RequestBody QueryPartCombCraftReq craftReq) throws Exception {
        if(null != craftReq.getPartCombCraftCode()) craftReq.setPartCombCraftCode(craftReq.getPartCombCraftCode().trim());
        if(null != craftReq.getPartCombCraftName()) craftReq.setPartCombCraftName(craftReq.getPartCombCraftName().trim());
        if(null != craftReq.getDesignPartCode()) craftReq.setDesignPartCode(craftReq.getDesignPartCode().trim());
        if(null != craftReq.getDesignPartName()) craftReq.setDesignPartName(craftReq.getDesignPartName().trim());

        List<PartCombCraft> craftDataResp = combCraftService.selectPartCombCraftChecklist(craftReq);
        Long size = Long.valueOf(craftDataResp.size());
        craftDataResp = PageUtils.startPage(craftDataResp, craftReq.getPage(), craftReq.getRows());
        return Result.ok(craftDataResp, size);
    }

    @ApiOperation(value = "selectPartCombCraftProgramDetail", notes = "获取清单页面部件组合工艺组合", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectPartCombCraftProgramDetail", method = RequestMethod.GET)
    public Result selectPartCombCraftProgramDetail(@RequestParam(value = "randomCode") String randomCode) throws Exception {
        notBlank(randomCode, "部件组合工艺randomCode不能为空");
        return combCraftService.selectPartCombCraftProgramDetail(randomCode);
    }

    @ApiOperation(value = "selectPartCombCraftData", notes = "获取部件组合工艺数据", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectPartCombCraftData", method = RequestMethod.GET)
    public Result selectPartCombCraftData(@RequestParam(value = "randomCode") String randomCode) throws Exception {
        notBlank(randomCode, "部件组合工艺randomCode不能为空");
        return combCraftService.selectPartCombCraftData(randomCode);
    }

    @ApiOperation(value = "selectPartCreateUser", notes = "获取部件组合工艺创建人", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectPartCreateUser", method = RequestMethod.GET)
    public Result selectPartCreateUser() throws Exception {
        return combCraftService.selectPartCreateUser();
    }

    @ApiOperation(value = "selectClothesCategoryData", notes = "获取服装品类数据", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectClothesCategoryData", method = RequestMethod.GET)
    public Result selectClothesCategoryData(String userCode) throws Exception {
        notBlank(userCode, "用户不能为空");
        CappUserDataAuthResp dataAuthResp = userService.getDataAuthResp(userCode);
        List<CappUserClothingCategory> clothings = dataAuthResp.getUserClothingCategories();
//        List<DictionaryVo> materialType = dictionaryService.getDictoryAll("ClothesCategory");
        return Result.ok(clothings);
    }

    @RequestMapping(value = "/selectPartDataByCategoryCode", method = RequestMethod.POST)
    @ApiOperation(value = "selectPartDataByCategoryCode", notes = "获取数据", httpMethod = "POST", response = Result.class)
    public Result selectPartDataByCategoryCode(String clothingCategoryCode,String[] codeArray) throws Exception {
        List<PartCombCraftAllDataResp> resps = combCraftService.selectPartDataByCategoryCode(clothingCategoryCode, codeArray);
        return Result.ok(resps);
    }
}
