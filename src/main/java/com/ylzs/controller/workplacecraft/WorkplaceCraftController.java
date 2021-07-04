package com.ylzs.controller.workplacecraft;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.elasticsearch.workplacecraft.dao.WorkplaceCraftESRepository;
import com.ylzs.elasticsearch.workplacecraft.esbo.WorkplaceCraftEsBO;
import com.ylzs.entity.workplacecraft.WorkplaceCraft;
import com.ylzs.service.craftstd.IMaxCodeService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseWorkplaceService;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.service.workplacecraft.IWorkplaceCraftService;
import com.ylzs.vo.workplacecraft.WorkplaceCraftExportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.constant.BusinessConstants.Status.*;
import static com.ylzs.common.util.Assert.*;

@Api(tags = "工位工序")
@RestController
@RequestMapping(value = "/workplace-craft")
public class WorkplaceCraftController implements IModuleInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkplaceCraftController.class);
    @Resource
    IWorkplaceCraftService workplaceCraftService;

    @Resource
    IMaxCodeService maxCodeService;

    @Resource
    IOperationLogService operationLogService;

    @Autowired
    private WorkplaceCraftESRepository workplaceCraftESRepository;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private SewingCraftWarehouseWorkplaceService sewingCraftWarehouseWorkplaceService;


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询所有工位工序")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<WorkplaceCraft>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                               @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                               @RequestParam(name = "productionPartRandomCode", required = false) Long productionPartRandomCode,
                                               @RequestParam(name = "keywords", required = false) String keywords,
                                               @RequestParam(name = "craftCategory", required = false) String craftCategory,
                                               @RequestParam(name = "craftCategoryList", required = false) String craftCategoryList,
                                               @RequestParam(name = "craftMainFrame", required = false) String craftMainFrame,
                                               @RequestParam(name = "craftFlowNum", required = false) Integer craftFlowNum,
                                               @RequestParam(name = "createDateStart", required = false) Date createDateStart,
                                               @RequestParam(name = "createDateStop", required = false) Date createDateStop,
                                               @RequestParam(name = "updateDateStart", required = false) Date updateDateStart,
                                               @RequestParam(name = "updateDateStop", required = false) Date updateDateStop,
                                               @RequestParam(name = "createUser", required = false) String createUser,
                                               @RequestParam(name = "updateUser", required = false) String updateUser,
                                               @RequestParam(name = "status", required = false) Integer status


    ) {
        Long mainFrameRandomCode = null;
        String mainFrameCode = "";
        if (StringUtils.isNotBlank(craftMainFrame)) {
            try {
                mainFrameRandomCode = Long.parseLong(craftMainFrame);
            } catch (Exception e) {
                mainFrameCode = craftMainFrame;
            }
        }
        PageHelper.startPage(page, rows);
        List<WorkplaceCraft> workplaceCrafts = workplaceCraftService.getByCondition(productionPartRandomCode, keywords,
                craftCategory, mainFrameRandomCode, mainFrameCode, craftFlowNum, createDateStart, createDateStop, updateDateStart, updateDateStop, createUser, updateUser, craftCategoryList, status);
        PageInfo<WorkplaceCraft> pageInfo = new PageInfo<>(workplaceCrafts);
        return Result.ok(workplaceCrafts, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getOne/{randomCode}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "查询单个工位工序")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<WorkplaceCraft> getOne(@PathVariable("randomCode") String randomCode) {
        notBlank(randomCode, "错误的工位工序关联代码");
        return Result.ok(workplaceCraftService.selectByPrimaryKey(Long.parseLong(randomCode)));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加工位工序")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<WorkplaceCraft> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                      @RequestBody WorkplaceCraft workplaceCraft) {
        notBlank(workplaceCraft.getWorkplaceCraftName(), "工位工序名称不能为空");
        notBlank(workplaceCraft.getProductionPartCode(), "生产部件代码不能为空");
        notBlank(workplaceCraft.getProductionPartName(), "生产部件名称不能为空");
        notNull(workplaceCraft.getProductionPartRandomCode(), "生产部件关联代码不能为空");
        notBlank(workplaceCraft.getCraftCategoryCode(), "工艺品类代码不能为空");
        notBlank(workplaceCraft.getCraftCategoryName(), "工艺品类名称不能为空");
        notNull(workplaceCraft.getCraftCategoryRandomCode(), "工艺品类关联代码不能为空");
        notBlank(workplaceCraft.getMainFrameCode(), "工艺主框架编码不能为空");
        notBlank(workplaceCraft.getMainFrameName(), "工艺主框架名称不能为空");
        notNull(workplaceCraft.getMainFrameRandomCode(), "工艺主框架关联代码不能为空");

        notNull(currentUser, "未取到用户信息");
        notNull(workplaceCraft.getCraftFlowNum(), "工序流序号不能为空");
        isFalse(workplaceCraftService.isFlowNumExistsInMainFrame(
                workplaceCraft.getCraftFlowNum(),
                workplaceCraft.getMainFrameRandomCode(),
                workplaceCraft.getRandomCode()
        ), "工艺主框架下工序流不能重复");
        isTrue(workplaceCraft.getCraftFlowNum().intValue() >= 100000, "工序流不能小于6位数字");

        isFalse(workplaceCraftService.isWorkplaceCraftNameExistsInMainFrame(
                workplaceCraft.getWorkplaceCraftName(),
                workplaceCraft.getMainFrameRandomCode(),
                workplaceCraft.getRandomCode()
        ), "工艺主框架下工位工序名称不能重复");


        Long randomCode = SnowflakeIdUtil.generateId();
        workplaceCraft.setRandomCode(randomCode);
        String preStr = workplaceCraft.getMainFrameCode().substring(0, 3) + workplaceCraft.getProductionPartCode().substring(0, 2);
        String workplaceCraftCode = maxCodeService.getNextSerialNo(getModuleCode(), preStr, 4, false);
        workplaceCraft.setWorkplaceCraftCode(workplaceCraftCode);
        workplaceCraft.setCreateTime(new Date());
        workplaceCraft.setCreateUser(currentUser.getUserName());
        workplaceCraft.setCreateUserName(currentUser.getUserName());
        workplaceCraft.setUpdateTime(workplaceCraft.getCreateTime());
        workplaceCraft.setUpdateUser(currentUser.getUserName());
        workplaceCraft.setUpdateUserName(currentUser.getUserName());
        workplaceCraft.setStatus(DRAFT_STATUS);
        workplaceCraft.setIsInvalid(false);
        int ret = workplaceCraftService.insert(workplaceCraft);
        if (ret != 1) {
            return Result.build(-1, "保存失败");
        } else {//保存成功，在ES把数据存到ES里面
            try {
                //randomCode作为ES中的id
                LOGGER.info("-------开始存入到ES中-------------");
                LOGGER.info("-------randomCode: " + workplaceCraft.getRandomCode() + "-------------");
                LOGGER.info("workplaceCraft: " + JSONObject.toJSONString(workplaceCraft));
                saveOrUpdateWorkplaceCraftToES(workplaceCraft);
                LOGGER.info("-------完成ES的存储-------------");
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("-------开始存入到ES中报错，错误信息是:" + e.getMessage());
            }

        }
        return Result.ok(workplaceCraft);
    }

    /**
     * 把WorkplaceCraft 实体存入到ES中，方便快速查询
     * id在ES中存在就更新，不存在就插入
     */
    private void saveOrUpdateWorkplaceCraftToES(WorkplaceCraft workplaceCraft) throws Exception {
        if (null == workplaceCraft) {
            return;
        }
        WorkplaceCraftEsBO craftEsBO = new WorkplaceCraftEsBO();
        //workplaceCraft的randdomCode作为存入Es中的ID
        craftEsBO.setId(workplaceCraft.getRandomCode());
        craftEsBO.setCraftCategoryName(workplaceCraft.getCraftCategoryName());
        craftEsBO.setCraftFlowNum(new Long(workplaceCraft.getCraftFlowNum()));
        craftEsBO.setMainFrameName(workplaceCraft.getMainFrameName());
        craftEsBO.setProductionPartCode(workplaceCraft.getProductionPartCode());
        craftEsBO.setWorkplaceCraftCode(workplaceCraft.getWorkplaceCraftCode());
        craftEsBO.setWorkplaceCraftName(workplaceCraft.getWorkplaceCraftName());
        if (workplaceCraft.getIsDefault() != null && (workplaceCraft.getIsDefault() == 0)) {
            craftEsBO.setDefault(0);
        } else {
            craftEsBO.setDefault(1);
        }

        workplaceCraftESRepository.save(craftEsBO);
    }

    private void deleteWorkplaceCraftToES(String[] randomCodes) throws Exception {
        if (null == randomCodes || randomCodes.length == 0) {
            return;
        }
        for (String random : randomCodes) {
            Long ran = Long.parseLong(random);
            if (null != ran) {
                workplaceCraftESRepository.deleteById(ran);
            }
        }
    }


    @RequestMapping(value = "/publish", method = RequestMethod.PUT)
    @ApiOperation(value = "publish", notes = "发布工位工序")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<WorkplaceCraft> publish(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                          @RequestParam(name = "randomCode", required = true) String randomCode) {
        notNull(randomCode, "关联代码不能为空");
        notNull(currentUser, "未取到用户信息");

        Long randomId = Long.parseLong(randomCode);
        WorkplaceCraft workplaceCraftOld = workplaceCraftService.selectByPrimaryKey(randomId);
        notNull(workplaceCraftOld, "未找到工位工序");
        Integer status = workplaceCraftOld.getStatus();
        if (status == null) {
            status = DRAFT_STATUS;
        }
        isTrue(status.equals(DRAFT_STATUS), "当前状态不允许发布");
        WorkplaceCraft workplaceCraft = new WorkplaceCraft();
        workplaceCraft.setRandomCode(randomId);
        workplaceCraft.setStatus(PUBLISHED_STATUS);
        workplaceCraft.setAuditTime(new Date());
        workplaceCraft.setAuditUser(currentUser.getUserName());
        workplaceCraft.setAuditUserName(currentUser.getUserName());
        workplaceCraft.setUpdateTime(new Date());
        workplaceCraft.setUpdateUser(currentUser.getUserName());
        workplaceCraft.setUpdateUserName(currentUser.getUserName());

        int ret = workplaceCraftService.updateByPrimaryKeySelective(workplaceCraft);
        if (ret != 1) {
            return Result.build(-1, "保存失败");
        } else {
            workplaceCraftService.updateRelateWorkplaceCraft(workplaceCraftOld.getWorkplaceCraftCode());
            //更新工序的工序流
//            sewingCraftWarehouseWorkplaceService.updateCraftFlowNum(workplaceCraftOld.getWorkplaceCraftCode(),
//                    workplaceCraftOld.getCraftFlowNum());
        }

        WorkplaceCraft workplaceCraftSave = workplaceCraftService.selectByPrimaryKey(randomId);
        return Result.ok(workplaceCraftSave);
    }




    @RequestMapping(value = "/cancel", method = RequestMethod.PUT)
    @ApiOperation(value = "cancel", notes = "作废工位工序")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<WorkplaceCraft> cancel(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                         @RequestParam(name = "randomCode", required = true) String randomCode) {
        notNull(randomCode, "关联代码不能为空");
        notNull(currentUser, "未取到用户信息");

        Long randomId = Long.parseLong(randomCode);
        WorkplaceCraft workplaceCraftOld = workplaceCraftService.selectByPrimaryKey(randomId);
        notNull(workplaceCraftOld, "未找到工位工序");

        Integer status = workplaceCraftOld.getStatus();
        if (status == null) {
            status = DRAFT_STATUS;
        }
        isTrue(!status.equals(INVALID_STATUS), "不允许重复作废");
        isFalse(workplaceCraftService.isSawingCraftWorkplaceExists(workplaceCraftOld.getWorkplaceCraftCode()), "工位工序已引用，不允许作废");
        WorkplaceCraft workplaceCraft = new WorkplaceCraft();
        workplaceCraft.setRandomCode(randomId);
        workplaceCraft.setStatus(INVALID_STATUS);
        workplaceCraft.setUpdateTime(new Date());
        workplaceCraft.setUpdateUser(currentUser.getUserName());
        workplaceCraft.setUpdateUserName(currentUser.getUserName());
        int ret = workplaceCraftService.updateByPrimaryKeySelective(workplaceCraft);
        if (ret != 1) {
            return Result.build(-1, "保存失败");
        }

        WorkplaceCraft workplaceCraftSave = workplaceCraftService.selectByPrimaryKey(randomId);
        return Result.ok(workplaceCraftSave);

    }


    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "修改工位工序")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<WorkplaceCraft> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                         @RequestBody WorkplaceCraft workplaceCraft) {
        notNull(workplaceCraft.getRandomCode(), "工位工序关联代码不能为空");
        notNull(currentUser, "未取到用户信息");
        WorkplaceCraft workplaceCraftOld = workplaceCraftService.selectByPrimaryKey(workplaceCraft.getRandomCode());
        notNull(workplaceCraftOld, "未找到工位工序");
        if (workplaceCraftOld.getStatus() != null) {
            isFalse(IN_VALID.equals(workplaceCraftOld.getStatus()), "已删除不允许修改");
        }
        if (workplaceCraft.getCraftFlowNum() != null) {
            notNull(workplaceCraft.getMainFrameRandomCode(), "工艺主框架关联代码不能为空");
            isFalse(workplaceCraftService.isFlowNumExistsInMainFrame(
                    workplaceCraft.getCraftFlowNum(),
                    workplaceCraft.getMainFrameRandomCode(),
                    workplaceCraft.getRandomCode()
            ), "工艺主框架下工序流不能重复");
            isTrue(workplaceCraft.getCraftFlowNum().intValue() >= 100000, "工序流不能小于6位数字");
        }
        if (workplaceCraft.getWorkplaceCraftName() != null) {
            notNull(workplaceCraft.getMainFrameRandomCode(), "工艺主框架关联代码不能为空");
            isFalse(workplaceCraftService.isWorkplaceCraftNameExistsInMainFrame(
                    workplaceCraft.getWorkplaceCraftName(),
                    workplaceCraft.getMainFrameRandomCode(),
                    workplaceCraft.getRandomCode()
            ), "工艺主框架下工位工序名称不能重复");
        }


        workplaceCraft.setStatus(DRAFT_STATUS);
        workplaceCraft.setIsInvalid(null);
        //更新ES中数据
        try {
            LOGGER.info("-------开始更新ES中数据-------------");
            LOGGER.info("-------workplaceCraft:" + JSONObject.toJSONString(workplaceCraft));
            saveOrUpdateWorkplaceCraftToES(workplaceCraft);
            LOGGER.info("-------更新ES中数据完成-------------");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("-------update到ES中报错，错误信息是:" + e.getMessage());
        }
        workplaceCraft.setId(null);
        workplaceCraft.setUpdateUser(currentUser.getUserName());
        workplaceCraft.setUpdateUserName(currentUser.getUserName());
        workplaceCraft.setUpdateTime(new Date());
        workplaceCraft.setAuditUserName(null);
        workplaceCraft.setAuditUser(null);
        workplaceCraft.setAuditTime(null);
        int ret = workplaceCraftService.updateByPrimaryKeySelective(workplaceCraft);
        if (ret != 1) {
            return Result.build(-1, "保存失败");
        }
        WorkplaceCraft workplaceCraftSave = workplaceCraftService.selectByPrimaryKey(workplaceCraft.getRandomCode());
        return Result.ok(workplaceCraftSave);
    }

    @RequestMapping(value = "/delete/{randomCodes}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除工位工序")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("randomCodes") String randomCodes) {
        notBlank(randomCodes, "工艺主框架关联代码不能为空");
        notNull(currentUser, "未取到用户信息");

        String[] split = randomCodes.split(",");
        for (String code : split) {
            WorkplaceCraft workCraft = workplaceCraftService.selectByPrimaryKey(Long.parseLong(code));
            notNull(workCraft, "未找到工位工序");
            String used = workplaceCraftService.getWorkplaceCraftInUsed(workCraft.getWorkplaceCraftCode());
            isTrue(StringUtils.isBlank(used), "工位工序已被引用，无法删除。" + used);
        }

                    LOGGER.info("-------开始删除ES中数据-------------");
        LOGGER.info("-------randomCodes: " + JSONObject.toJSONString(split) + "-------------");
        try {
            deleteWorkplaceCraftToES(split);
            LOGGER.info("-------删除ES中数据完成-------------");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("-------deleteES中报错，错误信息是:" + e.getMessage());
        }
        int ret = 0;
        WorkplaceCraft workplaceCraft = new WorkplaceCraft();
        workplaceCraft.setIsInvalid(true);
        workplaceCraft.setStatus(IN_VALID);
        workplaceCraft.setUpdateTime(new Date());
        workplaceCraft.setUpdateUser(currentUser.getUserName());
        workplaceCraft.setUpdateUserName(currentUser.getUserName());
        for (String code : split) {
            workplaceCraft.setRandomCode(Long.parseLong(code));
            ret += workplaceCraftService.updateByPrimaryKeySelective(workplaceCraft);
        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "export", notes = "导出工位工序")
    @Authentication(auth = Authentication.AuthType.EXPORT, required = false)
    public Result export(@RequestParam(name = "productionPartRandomCode", required = false) Long productionPartRandomCode,
                         @RequestParam(name = "keywords", required = false) String keywords,
                         @RequestParam(name = "craftCategory", required = false) String craftCategory,
                         @RequestParam(name = "craftMainFrame", required = false) String craftMainFrame,
                         @RequestParam(name = "craftFlowNum", required = false) Integer craftFlowNum,
                         @RequestParam(name = "createDateStart", required = false) Date createDateStart,
                         @RequestParam(name = "createDateStop", required = false) Date createDateStop,
                         @RequestParam(name = "updateDateStart", required = false) Date updateDateStart,
                         @RequestParam(name = "updateDateStop", required = false) Date updateDateStop,
                         @RequestParam(name = "createUser", required = false) String createUser,
                         @RequestParam(name = "updateUser", required = false) String updateUser
            , HttpServletResponse response) {
        Long mainFrameRandomCode = null;
        String mainFrameCode = "";
        if (StringUtils.isNotBlank(craftMainFrame)) {
            try {
                mainFrameRandomCode = Long.parseLong(craftMainFrame);
            }catch (Exception e){
                //前端传过来craftMainFrame 就是工艺主框架编码
                mainFrameCode = craftMainFrame;
            }
        }
        List<WorkplaceCraft> workplaceCrafts = workplaceCraftService.getByCondition(productionPartRandomCode, keywords,
                craftCategory, mainFrameRandomCode, mainFrameCode, craftFlowNum, createDateStart, createDateStop, updateDateStart, updateDateStop, createUser, updateUser, null, null);
        isFalse(workplaceCrafts.isEmpty(), "无数据导出");
        List<WorkplaceCraftExportVo> lst = workplaceCraftService.getWorkplaceCraftExportVos(workplaceCrafts);

        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名
            writer.addHeaderAlias("lineNo", "行号");
            writer.addHeaderAlias("craftCategoryName", "工艺品类");
            writer.addHeaderAlias("mainFrameName", "工艺主框架");
            writer.addHeaderAlias("productionPartName", "生产部件");
            writer.addHeaderAlias("craftFlowNum", "工序流序号");
            writer.addHeaderAlias("workplaceCraftCode", "工位工序代码");
            writer.addHeaderAlias("workplaceCraftName", "工位工序名称");
            writer.addHeaderAlias("remark", "备注");
            writer.addHeaderAlias("statusName", "状态");
            writer.addHeaderAlias("updateUser", "更新人");
            writer.addHeaderAlias("updateTime", "更新时间");
            writer.write(lst, true);

            String fileName = URLEncoder.encode("工位工序清单.xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            LOGGER.error("workplacecraft export fails", e);
        } finally {
            writer.close();
            if (null != out) {
                IoUtil.close(out);
            }
        }
        return null;
    }

    @RequestMapping(value = "/getWorkplaceCraftInUsed", method = RequestMethod.GET)
    @ApiOperation(value = "getWorkplaceCraftInUsed", notes = "获取工位工序使用")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result getWorkplaceCraftInUsed(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                         @RequestParam(name = "workplaceCraftCode", required = true) String workplaceCraftCode) {
        String ret = workplaceCraftService.getWorkplaceCraftInUsed(workplaceCraftCode);
        return Result.ok((ret!=null?ret:""));
    }


    @Override
    public String getModuleCode() {
        return "workplace-craft";
    }
}
