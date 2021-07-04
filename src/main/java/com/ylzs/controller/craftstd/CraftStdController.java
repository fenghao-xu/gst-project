package com.ylzs.controller.craftstd;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.craftstd.*;
import com.ylzs.service.craftstd.*;
import com.ylzs.service.system.IOperationLogService;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.craftstd.AssistanceToolAndTypeVo;
import com.ylzs.vo.craftstd.AssistanceToolVo;
import com.ylzs.vo.craftstd.CraftStdToolVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.*;
import static com.ylzs.common.util.Const.*;
import static com.ylzs.common.util.StringUtils.*;

/**
 * 说明：标准工艺接口
 *
 * @author lyq
 * 2019-09-30 16:52
 */
@Api(tags = "标准工艺")
@RestController
@RequestMapping(value = "/craft-std")
public class CraftStdController implements IModuleInfo {
    @Resource
    ICraftStdService craftStdService;
    @Resource
    IOperationLogService operationLogService;
    @Resource
    ICraftCategoryService craftCategoryService;
    @Resource
    ICraftPartService craftPartService;
    @Resource
    IMakeTypeService makeTypeService;
    @Resource
    IAssistanceToolService assistanceToolService;
    @Resource
    IDictionaryService dictionaryService;
    @Resource
    ICraftStdToolService craftStdToolService;

    @Resource
    ICraftFileService craftFileService;

//    @Resource
//    AutoUploadVideoSchedule autoUploadVideoSchedule;



    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping(value = "/getOne/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "getOne", notes = "根据ID查询工艺标准")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftStd>> getOne(@PathVariable("id") Long id) {
        notNull(id, "错误的id");
        long[] ids = new long[]{id};
        List<CraftStd> craftStds = craftStdService.getCraftStdById(ids);
        return Result.ok(craftStds);
    }

    @RequestMapping(value = "/getAssistanceToolAndType", method = RequestMethod.GET)
    @ApiOperation(value = "getAssistanceToolAndType", notes = "查询辅助工具和类型")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<JSONObject> getAssistanceToolAndType(@RequestParam(name = "craftStdId", required = false) String craftStdId) {
        List<AssistanceToolAndTypeVo> assistanceToolAndTypeVos = new ArrayList<>();
        List<DictionaryVo> dicList = dictionaryService.getDictoryAll("AssistanceToolType");
        List<AssistanceTool> toolList = assistanceToolService.list();

        for(DictionaryVo dic: dicList) {
            AssistanceToolAndTypeVo vo = new AssistanceToolAndTypeVo();
            vo.setToolTypeCode(dic.getDicValue());
            vo.setToolTypeName(dic.getValueDesc());

            List<AssistanceToolVo> curToolList = vo.getChildrens();
            if(curToolList == null) {
                curToolList = new ArrayList<>();
            }


            for(AssistanceTool tool: toolList) {
                if(vo.getToolTypeCode().equals(tool.getToolType())) {
                    AssistanceToolVo toolVo = new AssistanceToolVo();
                    toolVo.setToolCode(tool.getToolCode());
                    toolVo.setToolName(tool.getToolName());
                    curToolList.add(toolVo);
                }
            }
            vo.setChildrens(curToolList);
            assistanceToolAndTypeVos.add(vo);
        }


        List<CraftStdToolVo> craftStdToolVos = new ArrayList<>();
        Integer stdId = trimNull2Integer(craftStdId);
        if(stdId != null) {
            QueryWrapper<CraftStdTool> stdtoolWrapper = new QueryWrapper();
            stdtoolWrapper.lambda().eq(CraftStdTool::getCraftStdId,stdId).orderByAsc(CraftStdTool::getId);
            List<CraftStdTool> craftStdTools = craftStdToolService.list(stdtoolWrapper);
            for(CraftStdTool craftStdTool: craftStdTools) {
                CraftStdToolVo vo = new CraftStdToolVo();
                vo.setCraftStdId(craftStdTool.getCraftStdId());
                vo.setToolCode(craftStdTool.getToolCode());
                vo.setToolName(craftStdTool.getToolName());

                toolList.stream().filter(x->x.getToolCode().equals(craftStdTool.getToolCode())).findFirst().ifPresent(
                        a->{
                            dicList.stream().filter(xx->xx.getDicValue().equals(a.getToolType())).findFirst().ifPresent(
                                    aa->{
                                        vo.setToolTypeCode(aa.getDicValue());
                                        vo.setToolTypeName(aa.getValueDesc());

                                    }
                            );

                        }
                );



                craftStdToolVos.add(vo);
            }
        }

        //返回结果
        JSONObject result = new JSONObject();
        result.put("assistanceToolAndTypeVos", assistanceToolAndTypeVos);
        result.put("craftStdToolVos", craftStdToolVos);

        return Result.ok(result);
    }






    @RequestMapping(value = "/getCraftStdByCode/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "getCraftStdByCode", notes = "根据代码查询工艺标准")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftStd>> getCraftStdByCode(@PathVariable("code") String code) {
        notBlank(code, "错误的code");
        String[] codes = new String[]{code};
        List<CraftStd> craftStds = craftStdService.getCraftStdByCode(codes);
        return Result.ok(craftStds);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询全部工艺标准")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<CraftStd>> getAll(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "30") Integer rows,
                                         @RequestParam(name = "keywords", required = false) String keywords,
                                         @RequestParam(name = "beginDate", required = false) Date beginDate,
                                         @RequestParam(name = "endDate", required = false) Date endDate,
                                         @RequestParam(name = "id", required = false) Long id) {
        PageHelper.startPage(page, rows);
        List<CraftStd> craftStds = craftStdService.getCraftStdByPage(keywords, beginDate, endDate, id);
        PageInfo<CraftStd> pageInfo = new PageInfo<>(craftStds);
        return Result.ok(craftStds, pageInfo.getTotal());
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "add", notes = "添加工艺标准")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<CraftStd> add(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                @RequestBody CraftStd craftStd) {
        notNull(craftStd.getCraftPartIds(), "工艺部件id不能为空");
        notNull(craftStd.getMakeTypeId(), "做工类型id不能为空");
        notBlank(craftStd.getCraftStdName(), "工艺标准名称不能为空");


        MakeType makeType = makeTypeService.getMakeTypeById(craftStd.getMakeTypeId());
        notNull(makeType, "未找到做工类型");
        craftStd.setMakeTypeCode(makeType.getMakeTypeCode());
        craftStd.setIsEffectPic(null);
        craftStd.setIsHandPic(null);
        craftStd.setIsVideo(null);
        craftStd.setIsCategoryShare(null);
        craftStd.setIsPartShare(null);
        craftStd.setCreateTime(new Date());
        craftStd.setCreateUser(currentUser.getUser());
        craftStd.setStatus(STD_STATUS_NEW);
        Integer ret = craftStdService.addCraftStd(craftStd);
        if (ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        } else {
            if (ret > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.CREATE,
                        currentUser.getUser(), "添加工艺标准" + craftStd.getCraftStdCode());
            }
        }

        return Result.ok(craftStd);
    }


    @RequestMapping(value = "/addData", method = RequestMethod.POST)
    @ApiOperation(value = "addData", notes = "添加工艺标准")
    @Authentication(auth = Authentication.AuthType.INSERT, required = true)
    public Result<CraftStd> addData(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                    @RequestParam(name = "craftStdName", required = false) String craftStdName,
                                    @RequestParam(name = "sourceCode", required = false) String sourceCode,
                                    @RequestParam(name = "suggestCode", required = false) String suggestCode,
                                    @RequestParam(name = "craftPartIds", required = false) String craftPartIds,
                                    @RequestParam(name = "sectionId", required = false) String sectionId,
                                    @RequestParam(name = "workTypeId", required = false) String workTypeId,
                                    @RequestParam(name = "makeTypeId", required = false) String makeTypeId,
                                    @RequestParam(name = "requireQuality", required = false) String requireQuality,
                                    @RequestParam(name = "lineId", required = false) String lineId,
                                    @RequestParam(name = "ironTemperature", required = false) String ironTemperature,
                                    @RequestParam(name = "makeDesc", required = false) String makeDesc,
                                    @RequestParam(name = "stitchLengthId", required = false) String stitchLengthId,
                                    @RequestParam(name = "machineId", required = false) String machineId,
                                    @RequestParam(name = "effectPicUrls", required = false) String effectPicUrls,
                                    @RequestParam(name = "handPicUrls", required = false) String handPicUrls,
                                    @RequestParam(name = "videoUrl", required = false) String videoUrl,
                                    @RequestParam(name = "toolCodes", required = false) String toolCodes,
                                    @RequestParam(name = "toolNames", required = false) String toolNames,
                                    @RequestParam(name = "isKeyCraft", required = false) Integer isKeyCraft,
                                    @RequestParam(name = "isNormalCraft", required = false) Integer isNormalCraft) {

        notBlank(craftStdName, "工艺标准名称不能为空");
        notBlank(craftPartIds, "部件id不能为空");
        isFalse(craftStdService.isCraftStdNameExists(craftStdName,null), "工艺标准名称不能重复");

        if(toolCodes != null && !toolCodes.isEmpty()) {
            String[] codes = toolCodes.split(",");
            //查询是否有重复
            if (codes.length > 1) {
                for (int i = 1; i < codes.length; i++) {
                    for (int j = 0; j < i; j++) {
                        isTrue(!codes[i].equals(codes[j]), "辅助工具代码不能重复");
                    }
                }
            }
        }



        CraftStd craftStd = new CraftStd();
        craftStd.setCraftStdName(craftStdName);
        craftStd.setCraftPartIds(craftPartIds);
        craftStd.setSectionId(trimNull2Integer(sectionId));
        craftStd.setWorkTypeId(trimNull2Integer(workTypeId));
        if(makeTypeId != null) {
            MakeType makeType = makeTypeService.getMakeTypeById(trimNull2Integer(makeTypeId));
            notNull(makeType, "未找到做工类型");
            craftStd.setMakeTypeId(trimNull2Integer(makeTypeId));
            craftStd.setMakeTypeCode(makeType.getMakeTypeCode());
        }
        craftStd.setSourceCode(sourceCode);
        craftStd.setSuggestCode(suggestCode);
        craftStd.setRequireQuality(requireQuality);
        craftStd.setLineId(trimNull2Integer(lineId));

        craftStd.setIronTemperature(String2Decimal(ironTemperature));
        craftStd.setMakeDesc(makeDesc);
        craftStd.setStitchLengthId(trimNull2Integer(stitchLengthId));
        craftStd.setMachineId(trimNull2Integer(machineId));
        craftStd.setIsEffectPic(null);
        craftStd.setIsHandPic(null);
        craftStd.setIsVideo(null);
        craftStd.setIsCategoryShare(null);
        craftStd.setIsPartShare(null);
        if(isKeyCraft != null) {
            craftStd.setIsKeyCraft(isKeyCraft.equals(1));
        }
        if(isNormalCraft != null) {
            craftStd.setIsNormalCraft(isNormalCraft.equals(1));
        }
        craftStd.setCreateTime(new Date());
        craftStd.setCreateUser(currentUser.getUser());
        craftStd.setStatus(Const.STD_STATUS_NEW);
        Integer ret = craftStdService.addCraftStdData(craftStd, NotNullString(effectPicUrls), NotNullString(handPicUrls), NotNullString(videoUrl), toolCodes,toolNames);
        if (ret == null || ret == 0) {
            return Result.build(0, "无法重复添加");
        } else {
            if (ret > 0) {
                operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.CREATE,
                        currentUser.getUser(), "添加工艺标准" + craftStd.getCraftStdCode());
            }
        }
        return Result.ok(craftStd);
    }




    @RequestMapping(value = "/updateData", method = RequestMethod.PUT)
    @ApiOperation(value = "updateData", notes = "更新工艺标准")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<CraftStd> updateData(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                       @RequestParam(name = "id", required = true) Long id,
                                       @RequestParam(name = "craftStdName", required = false) String craftStdName,
                                       @RequestParam(name = "sourceCode", required = false) String sourceCode,
                                       @RequestParam(name = "suggestCode", required = false) String suggestCode,
                                       @RequestParam(name = "craftPartIds", required = false) String craftPartIds,
                                       @RequestParam(name = "sectionId", required = false) String sectionId,
                                       @RequestParam(name = "workTypeId", required = false) String workTypeId,
                                       @RequestParam(name = "makeTypeId", required = false) String makeTypeId,
                                       @RequestParam(name = "requireQuality", required = false) String requireQuality,
                                       @RequestParam(name = "lineId", required = false) String lineId,
                                       @RequestParam(name = "ironTemperature", required = false) String ironTemperature,
                                       @RequestParam(name = "makeDesc", required = false) String makeDesc,
                                       @RequestParam(name = "stitchLengthId", required = false) String stitchLengthId,
                                       @RequestParam(name = "machineId", required = false) String machineId,
                                       @RequestParam(name = "effectPicUrls", required = false) String effectPicUrls,
                                       @RequestParam(name = "handPicUrls", required = false) String handPicUrls,
                                       @RequestParam(name = "videoUrl", required = false) String videoUrl,
                                       @RequestParam(name = "toolCodes", required = false) String toolCodes,
                                       @RequestParam(name = "toolNames", required = false) String toolNames,
                                       @RequestParam(name = "isKeyCraft", required = false) Integer isKeyCraft,
                                       @RequestParam(name = "isNormalCraft", required = false) Integer isNormalCraft) {
        notNull(id, "工艺标准不能为空");
        notBlank(craftPartIds, "部件id不能为空");
        long[] ids = new long[]{id};
        List<CraftStd> craftStds = craftStdService.getCraftStdById(ids);
        notNull(craftStds, "未找到工艺标准");
        isTrue(craftStds.size() > 0, "未找到工艺标准");
        if(craftStdName!= null && !craftStdName.isEmpty()) {
            isFalse(craftStdService.isCraftStdNameExists(craftStdName, id), "工艺标准名称不能重复");
        }

        if(toolCodes != null && !toolCodes.isEmpty()) {
            String[] codes = toolCodes.split(",");
            //查询是否有重复
            if (codes.length > 1) {
                for (int i = 1; i < codes.length; i++) {
                    for (int j = 0; j < i; j++) {
                        isTrue(!codes[i].equals(codes[j]), "辅助工具代码不能重复");
                    }
                }
            }
        }

        CraftStd craftStdOld = craftStds.get(0);
        isTrue(craftStdOld.getStatus() == null
                || STD_STATUS_NEW.equals(craftStdOld.getStatus())
                || STD_STATUS_BACK.equals(craftStdOld.getStatus())
                || STD_STATUS_OLD.equals(craftStdOld.getStatus()), "当前状态不允许修改");

        CraftStd craftStd = new CraftStd();
        craftStd.setId(id);
        craftStd.setCraftStdName(craftStdName);
        craftStd.setCraftPartIds(craftPartIds);
        craftStd.setSectionId(trimNull2Integer(sectionId));
        craftStd.setWorkTypeId(trimNull2Integer(workTypeId));
        craftStd.setMakeTypeId(trimNull2Integer(makeTypeId));
        craftStd.setRequireQuality(requireQuality);
        craftStd.setSourceCode(trimNull(sourceCode));
        craftStd.setSuggestCode(trimNull(suggestCode));
        craftStd.setLineId(trimNull2Integer(lineId));
        craftStd.setIronTemperature(String2Decimal(ironTemperature));
        craftStd.setMakeDesc(makeDesc);
        craftStd.setStitchLengthId(trimNull2Integer(stitchLengthId));
        craftStd.setMachineId(trimNull2Integer(machineId));
        craftStd.setIsEffectPic(null);
        craftStd.setIsHandPic(null);
        craftStd.setIsVideo(null);
        craftStd.setIsCategoryShare(null);
        craftStd.setIsPartShare(null);
        if(isKeyCraft != null) {
            craftStd.setIsKeyCraft(isKeyCraft.equals(1));
        }
        if(isNormalCraft != null) {
            craftStd.setIsNormalCraft(isNormalCraft.equals(1));
        }
        craftStd.setUpdateTime(new Date());
        craftStd.setUpdateUser(currentUser.getUser());
        craftStd.setStatus(STD_STATUS_NEW);
        Integer ret = craftStdService.updateCraftStdData(craftStd, craftStdOld, NotNullString(effectPicUrls), NotNullString(handPicUrls), NotNullString(videoUrl), toolCodes, toolNames);
        if (ret > 0) {
            operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.UPDATE,
                    currentUser.getUser(), "更新工艺标准" + craftStd.getCraftStdCode());
        }
        return Result.ok(craftStd);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation(value = "update", notes = "更新工艺标准")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<CraftStd> update(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                   @RequestBody CraftStd craftStd) {
        notNull(craftStd.getId(), "工艺标准id不能为空");

        long[] ids = new long[]{craftStd.getId()};
        List<CraftStd> craftStds = craftStdService.getCraftStdById(ids);
        notNull(craftStds, "未找到工艺标准");
        isTrue(craftStds.size() > 0, "未找到工艺标准");

        CraftStd craftStdOld = craftStds.get(0);
        isTrue(craftStdOld.getStatus() == null
                || STD_STATUS_NEW.equals(craftStdOld.getStatus())
                || STD_STATUS_BACK.equals(craftStdOld.getStatus())
                || STD_STATUS_OLD.equals(craftStdOld.getStatus()), "当前状态不允许修改");

        craftStd.setIsEffectPic(null);
        craftStd.setIsHandPic(null);
        craftStd.setIsVideo(null);
        craftStd.setIsCategoryShare(null);
        craftStd.setIsPartShare(null);
        craftStd.setUpdateTime(new Date());
        craftStd.setUpdateUser(currentUser.getUser());
        craftStd.setStatus(STD_STATUS_NEW);


        Integer ret = craftStdService.updateCraftStd(craftStd, craftStdOld);
        if (ret > 0) {
            operationLogService.addLog(getModuleCode(), IOperationLogService.ActionType.UPDATE,
                    currentUser.getUser(), "更新工艺标准" + craftStdOld.getCraftStdCode());
        }
        return Result.ok(craftStd);
    }

    @RequestMapping(value = "/deleteResource", method = RequestMethod.PUT)
    @ApiOperation(value = "deleteResource", notes = "删除文件资源")
    @Authentication(auth = Authentication.AuthType.DELETE, required = true)
    public Result<Integer> deleteResource(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                          @RequestParam(name = "id", required = false) Long id,
                                          @RequestParam(name = "resourceType", required = true) Integer resourceType,
                                          @RequestParam(name = "fileUrl", required = true) String fileUrl) {
        notNull(resourceType, "资源类型不能为空");
        notBlank(fileUrl, "文件访问路径不能为空");
        Integer ret;
        if(id != null) {
            ret = craftStdService.deleteResource(id, resourceType, fileUrl, currentUser.getUser());
        } else {
            ret = craftStdService.deleteResourceSimple(resourceType, fileUrl, currentUser.getUser());
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/addResouce", method = RequestMethod.PUT)
    @ApiOperation(value = "addResouce", notes = "添加文件资源")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<String> addResouce(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                     @RequestParam(name = "id", required = false) Long id,
                                     @RequestParam(name = "resourceType", required = true) Integer resourceType,
                                     @RequestParam(name = "file", required = true) MultipartFile file
    ) {
        notNull(resourceType, "资源类型不能为空");
        notNull(file, "非法文件");
        String ret;
        if (id != null) {
            ret = craftStdService.addResource(id, resourceType, file, currentUser.getUser());
        } else {
            ret = craftStdService.addResourceSimple(resourceType, file, currentUser.getUser());
        }
        return Result.ok(ret);
    }


    @RequestMapping(value = "/addResoucePath", method = RequestMethod.POST)
    @ApiOperation(value = "addResoucePath", notes = "添加文件资源路径")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<String> addResoucePath(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
//                                      @RequestParam(name = "工艺标准ID", required = true) Long id,
//                                      @RequestParam(name = "资源类型 10线稿图 11线稿图2 20效果图 21效果图2 30视频") Integer resourceType,
//                                      @RequestParam(name = "文件共享路径", required = true) String sharePath
                                         @RequestBody String jsonString
    ) {

        JSONObject jsonData = JSONObject.parseObject(jsonString);
        Long id = jsonData.getLong("id");
        Integer resourceType = jsonData.getInteger("resourceType");
        String sharePath = jsonData.getString("sharePath");

        notNull(resourceType, "资源类型不能为空");
        notBlank(sharePath, "非法的共享路径");
        String ret;
        if (id != null) {
            ret = craftStdService.addResourcePath(id, resourceType, sharePath, currentUser.getUser());
        } else {
            ret = craftStdService.addResourcePathSimple(resourceType, sharePath, currentUser.getUser());
        }
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getResoucePath", method = RequestMethod.GET)
    @ApiOperation(value = "getResoucePath", notes = "获取文件资源路径")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<List<ICraftStdService.ShareFileInfo>> getResoucePath(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                                                       @RequestParam(name = "resourceType", required = true) Integer resourceType,
                                                                       @RequestParam(name = "sharePath", required = false) String sharePath
    ) {
        notNull(resourceType, "资源类型不能为空");
        //notBlank(sharePath, "非法的共享路径");
        if (StringUtils.isEmpty(sharePath)) {
            String basePath = StaticDataCache.getInstance().getValue(Const.SHARE_BASE_PATH, "");
            sharePath = "smb://" + basePath; // 10.7.200.135\share
        }
        try {
            if (resourceType != null && resourceType == 30) {
                return Result.ok(StaticDataCache.MP4_FILE_INFO_MAP.get(sharePath));
            } else {
                return Result.ok(StaticDataCache.PIC_FILE_INFO_MAP.get(sharePath));
            }
        } catch (Exception e) {
            return Result.build(0, e.getMessage());
        }
    }


    @RequestMapping(value = "/getCraftStdStatistic", method = RequestMethod.GET)
    @ApiOperation(value = "getCraftStdStatistic", notes = "获取工序统计信息")
    @Authentication(auth = Authentication.AuthType.QUERY, required = true)
    public Result<CraftStdStatistic> getCraftStdStatistic(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser
    ) {
        CraftStdStatistic craftStdStatistic = craftStdService.getCraftStdStatistic();
        return Result.ok(craftStdStatistic);
    }


    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete", notes = "删除工艺标准")
    @Authentication(auth = Authentication.AuthType.EDIT, required = true)
    public Result<Integer> delete(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                  @PathVariable("ids") String ids) {
        notBlank(ids, "非法id");
        String[] split = ids.split(",");
        isTrue(split.length > 0, "非法id");
        long[] craftStdIds = Arrays.stream(split).mapToLong(Long::parseLong).toArray();
        int ret = craftStdService.deleteCraftStd(craftStdIds, currentUser.getUser(), getModuleCode());
        return Result.ok(ret);
    }

    @RequestMapping(value = "/getCraftStdInUsed/{ids}", method = RequestMethod.GET)
    @ApiOperation(value = "getCraftStdInUsed", notes = "查询工序建标是否被引用")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<String> getCraftStdInUsed(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                            @PathVariable("ids") String ids) {
        notBlank(ids, "非法id");
        String[] split = ids.split(",");
        isTrue(split.length > 0, "非法id");
        long[] craftStdIds = Arrays.stream(split).mapToLong(Long::parseLong).toArray();
        List<CraftStd> craftStds = craftStdService.getCraftStdById(craftStdIds);
        isTrue(craftStdIds.length == craftStds.size(), "未找到工序");
        String allUsed = "";
        for(CraftStd std: craftStds) {
            String used = craftStdService.getCraftStdInUsed(std.getCraftStdCode());
            if(StringUtils.isNotBlank(used)) {
                allUsed += used;
            }
        }

        return Result.ok(allUsed);
    }



    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "getByCondition", notes = "模糊查询工艺标准")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<CraftStd>> getByCondition(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "30") Integer rows,
                                                 @RequestParam(name = "craftStdCode", required = false) String craftStdCode,
                                                 @RequestParam(name = "craftStdName", required = false) String craftStdName,
                                                 @RequestParam(name = "styleCode", required = false) String styleCode,
                                                 @RequestParam(name = "requireQuality", required = false) String requireQuality,
                                                 @RequestParam(name = "makeDesc", required = false) String makeDesc,
                                                 @RequestParam(name = "status", required = false) String status,
                                                 @RequestParam(name = "createTimeBeginDate", required = false) Date createTimeBeginDate,
                                                 @RequestParam(name = "createTimeEndDate", required = false) Date createTimeEndDate,
                                                 @RequestParam(name = "okTimeBeginDate", required = false) Date okTimeBeginDate,
                                                 @RequestParam(name = "okTimeEndDate", required = false) Date okTimeEndDate,
                                                 @RequestParam(name = "updateTimeBeginDate", required = false) Date updateTimeBeginDate,
                                                 @RequestParam(name = "updateTimeEndDate", required = false) Date updateTimeEndDate,
                                                 @RequestParam(name = "remark", required = false) String remark,
                                                 @RequestParam(name = "isVideo", required = false) Boolean isVideo,
                                                 @RequestParam(name = "isPic", required = false) Boolean isPic,
                                                 @RequestParam(name = "commitTimeBeginDate", required = false) Date commitTimeBeginDate,
                                                 @RequestParam(name = "commitTimeEndDate", required = false) Date commitTimeEndDate,
                                                 @RequestParam(name = "user", required = false) String user,
                                                 @RequestParam(name = "extractTimeBeginDate", required = false) Date extractTimeBeginDate,
                                                 @RequestParam(name = "extractTimeEndDate", required = false) Date extractTimeEndDate,
                                                 @RequestParam(name = "craftCategoryId", required = false) Integer craftCategoryId,
                                                 @RequestParam(name = "craftPartId", required = false) Integer craftPartId,
                                                 @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                                 @RequestParam(name = "craftPartCode", required = false) String craftPartCode

    ) {

        PageHelper.startPage(page, rows);
        List<CraftStd> craftStds = craftStdService.getCraftStdByCondition(craftStdCode,
                craftStdName, styleCode, requireQuality, makeDesc, status,
                createTimeBeginDate, createTimeEndDate, okTimeBeginDate, okTimeEndDate,
                updateTimeBeginDate, updateTimeEndDate,
                remark, isVideo, isPic, commitTimeBeginDate, commitTimeEndDate, user,
                extractTimeBeginDate, extractTimeEndDate, craftCategoryId, craftPartId, craftCategoryCode, craftPartCode);
        PageInfo<CraftStd> pageInfo = new PageInfo<>(craftStds);
        return Result.ok(craftStds, pageInfo.getTotal());
    }

    @RequestMapping(value = "/commit", method = RequestMethod.PUT)
    @ApiOperation(value = "commit", notes = "提交工艺")
    @Authentication(auth = Authentication.AuthType.COMMIT, required = true)
    public Result<Integer> commit(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                          @RequestBody String jsonString) {
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        Long id = jsonData.getLong("id");
        notNull(id, "id不允许为空");
        Result<Integer> ret = craftStdService.commitCraftStd(id, true, currentUser.getUser(), null);
        return ret;

    }

    @RequestMapping(value = "/withdraw", method = RequestMethod.PUT)
    @ApiOperation(value = "withdraw", notes = "撤回工艺")
    @Authentication(auth = Authentication.AuthType.CANCEL, required = true)
    public Result<Integer> withdraw(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                            @RequestBody String jsonString) {
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        Long id = jsonData.getLong("id");
        String rejectReason = jsonData.getString("reason");

        notNull(id, "id不允许为空");
        Result<Integer> ret = craftStdService.commitCraftStd(id, false, currentUser.getUser(), rejectReason);
        return ret;
    }

    @RequestMapping(value = "/audit", method = RequestMethod.PUT)
    @ApiOperation(value = "audit", notes = "审核工艺")
    @Authentication(auth = Authentication.AuthType.COMMIT, required = true)
    public Result<Integer> audit(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                         @RequestBody String jsonString) {
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        Long id = jsonData.getLong("id");

        notNull(id, "id不允许为空");
        Result<Integer> ret = craftStdService.auditCraftStd(id, true, currentUser.getUser());
        return ret;
    }

    @RequestMapping(value = "/unaudit", method = RequestMethod.PUT)
    @ApiOperation(value = "unaudit", notes = "反审核工艺")
    @Authentication(auth = Authentication.AuthType.UNAUTH, required = true)
    public Result<Integer> unaudit(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = false) UserContext currentUser,
                                           @RequestBody String jsonString) {
        JSONObject jsonData = JSONObject.parseObject(jsonString);
        Long id = jsonData.getLong("id");

        notNull(id, "id不允许为空");
        Result<Integer> ret = craftStdService.auditCraftStd(id, false, currentUser.getUser());
        return ret;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation(value = "test", notes = "开发人员测试")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result test() {
        //autoUploadVideoSchedule.test();
//        String fileName = "XZJTJ126-1.jpeg";
//        int rightPos = fileName.lastIndexOf("-");
//        String code = fileName.substring(0, rightPos);
//        return Result.ok(code);
//        List<CraftFile> craftFiles = craftFileService.getDuplicateFile();
//        for(CraftFile craftFile: craftFiles) {
//            Integer ret = craftFileService.deleteCraftFile(craftFile);
//        }
        return null;
    }


    @Override
    public String getModuleCode() {
        return "craft-std";
    }



}