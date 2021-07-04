package com.ylzs.controller.processCombCraft;

import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.util.pageHelp.PageUtils;
import com.ylzs.entity.processCombCraft.ProcessCombCraft;
import com.ylzs.entity.processCombCraft.req.ProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.req.QueryProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.req.UpdateProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.resp.ProcessCombCraftAllDataResp;
import com.ylzs.service.processCombCraft.IProcessCombCraftService;
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
 * @Date 2020/3/14
 */
@Api(tags = "工序组合工艺")
@RestController
@RequestMapping("/processCombCraft")
public class ProcessCombCraftController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessCombCraftController.class);

    @Resource
    private IProcessCombCraftService combCraftService;

    @ApiOperation(value = "addProcessCombCraft", notes = "新增工序组合工艺数据", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/addProcessCombCraft", method = RequestMethod.POST)
    public Result addProcessCombCraft(@RequestBody ProcessCombCraftReq req) throws Exception {
        return combCraftService.addProcessCombCraft(req);
    }

    @ApiOperation(value = "selectProcessCombCraftData", notes = "获取工序组合工艺数据", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectProcessCombCraftData", method = RequestMethod.GET)
    public Result selectProcessCombCraftData(@RequestParam (value = "randomCode") String randomCode) throws Exception {
        notBlank(randomCode,"工序组合工艺randomCode不能为空");
        return combCraftService.selectProcessCombCraftData(randomCode);
    }

    @ApiOperation(value = "updateProcessCombCraft", notes = "修改工序组合工艺状态", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/updateProcessCombCraft", method = RequestMethod.POST)
    public Result updateProcessCombCraft(@RequestBody UpdateProcessCombCraftReq req) throws Exception {
        //删除 草稿
        if(req.getOperateType() == BusinessConstants.CommonConstant.ONE){
            if(req.getRandomCode().longValue() == 0L){
                LOGGER.info("无工序组合工艺草稿【{}】");
                return Result.ok();
            }
        }else{
            if(req.getRandomCode()== null)
                return Result.build(BusinessConstants.CommonConstant.ZERO,"工序组合工艺randomCode不能为空");
        }
        if(StringUtils.isBlank(req.getUserCode())){
            return Result.build(BusinessConstants.CommonConstant.ZERO, "用户信息为空，不能操作");
        }
        return combCraftService.updateProcessCombCraft(req);
    }

    @ApiOperation(value = "selectProcessCombCraftChecklist", notes = "获取部件组合工艺数据清单", httpMethod = "POST", response = Result.class)
    @RequestMapping(value = "/selectProcessCombCraftChecklist", method = RequestMethod.POST)
    public Result selectProcessCombCraftChecklist(@RequestBody QueryProcessCombCraftReq craftReq) throws Exception {
        if(null != craftReq.getCraftCode()) craftReq.setCraftCode(craftReq.getCraftCode().trim());
        if(null != craftReq.getCraftDescript()) craftReq.setCraftDescript(craftReq.getCraftDescript().trim());
        if(null != craftReq.getCraftCombCraftCode()) craftReq.setCraftCombCraftCode(craftReq.getCraftCombCraftCode().trim());
        if(null != craftReq.getCraftCombCraftName()) craftReq.setCraftCombCraftName(craftReq.getCraftCombCraftName().trim());

        List<ProcessCombCraft> craftDataResp = combCraftService.selectProcessCombCraftChecklist(craftReq);
        Long size = Long.valueOf(craftDataResp.size());
        craftDataResp = PageUtils.startPage(craftDataResp, craftReq.getPage(), craftReq.getRows());
        return Result.ok(craftDataResp, size);
    }

    @ApiOperation(value = "selectProcessCombCraftProgram", notes = "获取清单页面工序组合工艺组合", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectProcessCombCraftProgram", method = RequestMethod.GET)
    public Result selectProcessCombCraftProgram(@RequestParam(value = "randomCode") String randomCode) throws Exception {
        notBlank(randomCode,"工序组合工艺randomCode不能为空");
        return combCraftService.selectProcessCombCraftProgram(randomCode);
    }

    @ApiOperation(value = "selectProcessCreateUser", notes = "获取工序组合工艺创建人", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/selectProcessCreateUser", method = RequestMethod.GET)
    public Result selectProcessCreateUser() throws Exception {
        return combCraftService.selectProcessCreateUser();
    }

    @RequestMapping(value = "/selectProcessDataByCategoryCode", method = RequestMethod.POST)
    @ApiOperation(value = "selectProcessDataByCategoryCode", notes = "获取数据", httpMethod = "POST", response = Result.class)
    public Result selectProcessDataByCategoryCode(String clothingCategoryCode) throws Exception {
        List<ProcessCombCraftAllDataResp> resps = combCraftService.selectProcessDataByCategoryCode(clothingCategoryCode);
        return Result.ok(resps);
    }

}
