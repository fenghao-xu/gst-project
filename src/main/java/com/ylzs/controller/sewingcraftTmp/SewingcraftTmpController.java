package com.ylzs.controller.sewingcraftTmp;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.entity.craftstd.MakeType;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.timeparam.SameLevelCraft;
import com.ylzs.service.craftstd.IMachineService;
import com.ylzs.service.craftstd.IMakeTypeService;
import com.ylzs.service.sewingcraftTmp.SewingcraftTmpService;
import com.ylzs.service.timeparam.impl.SameLevelCraftService;
import com.ylzs.vo.SewingCraftResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-10-15 10:30
 */
@Api(tags = "工序临时库")
@RestController
@RequestMapping("/sewingCraftTmpLibrary")
public class SewingcraftTmpController {
    @Resource
    private SewingcraftTmpService sewingcraftTmpService;

    @Resource
    private SameLevelCraftService sameLevelCraftService;

    @Resource
    private IMakeTypeService makeTypeService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询工序临时库列表")
    public Result<List<SewingCraftWarehouse>> getAll(@RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "30") Integer rows,
                                                     @RequestParam(name = "craftCode", required = false) String craftCode,
                                                     @RequestParam(name = "description", required = false) String description,
                                                     @RequestParam(name = "craftCategoryCode", required = false) String craftCategoryCode,
                                                     @RequestParam(name = "craftPartCode", required = false) String craftPartCode,
                                                     @RequestParam(name = "hasPicture", required = false) Boolean hasPicture,
                                                     @RequestParam(name = "hasVedio", required = false) Boolean hasVedio,
                                                     @RequestParam(name = "sysnStatus", required = false) Integer sysnStatus) {
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotEmpty(craftCode)) {
            craftCode = StringUtils.replaceBlank(craftCode);
        }
        param.put("craftCode", craftCode);

        if (StringUtils.isNotEmpty(description)) {
            description = StringUtils.replaceBlank(description);
        }
        if (sysnStatus != null) {
            param.put("synStatus", sysnStatus);
        }
        param.put("description", description);
        if (hasPicture != null && Boolean.TRUE.equals(hasPicture)) {
            param.put("hasPicture", hasPicture);
        }
        if (hasVedio != null && Boolean.TRUE.equals(hasVedio)) {
            param.put("hasVedio", hasVedio);
        }
        param.put("craftCategoryCode", craftCategoryCode);
        param.put("craftPartCode", craftPartCode);
        PageHelper.startPage(page, rows);
        List<SewingCraftWarehouse> data = sewingcraftTmpService.getDataByPager(param);
        if (null != data && data.size() > 0) {
            Map<String, SameLevelCraft> sameLevelCraftMap = sameLevelCraftService.getAllDataToMap();
            Map<String, MakeType> makeTypeMap = makeTypeService.getMakeTypeMap();
            data.stream().parallel().forEach(x -> {
                if (sameLevelCraftMap.containsKey(x.getSameLevelCraftNumericalCode())) {
                    x.setSameLevelCraftName(sameLevelCraftMap.get(x.getSameLevelCraftNumericalCode()).getSameLevelCraftName());
                }
                if (makeTypeMap.containsKey(x.getMakeTypeCode())) {
                    x.setMakeTypeName(makeTypeMap.get(x.getMakeTypeCode()).getMakeTypeName());
                }
                if (x.getIsHandPic() == null) {
                    x.setIsHandPic(Boolean.FALSE);
                }
                if (x.getIsVideo() == null) {
                    x.setIsVideo(Boolean.FALSE);
                }
                //查所选择的建标系统的 线稿图和视频
                getSketchPicAndVedio(x);
                List<String> workplaceNameList = sewingcraftTmpService.getWorkplaceBySewingRandomAndCraft(x.getRandomCode(), x.getCraftCode());
                x.setWorkplaceNameList(workplaceNameList);
            });
        }
        PageInfo<SewingCraftWarehouse> pageInfo = new PageInfo<>(data);
        return Result.ok(data, pageInfo.getTotal());

    }

    /**
     * 查询单个车缝工序，选中的建标
     */
    private void getSketchPicAndVedio(SewingCraftWarehouse vo) {
        if (null == vo) {
            return;
        }
        if (null == vo.getSketchPicUrl()) {
            vo.setSketchPicUrl(new ArrayList<>());
        }
        if (null == vo.getVedioUrl()) {
            vo.setVedioUrl(new ArrayList<>());
        }
        //查所选择的建标系统的 线稿图和视频
        List<SewingCraftResource> resources = sewingcraftTmpService.getSewingStdPicAndVedio(vo.getRandomCode(), vo.getCraftCode());
        if (null != resources && resources.size() > 0) {
            for (SewingCraftResource res : resources) {
                if (res.getResouceType().intValue() == Const.RES_TYPE_HAND_IMG) {
                    vo.getSketchPicUrl().add(res.getUrl());
                } else if (res.getResouceType().intValue() == Const.RES_TYPE_STD_VIDEO) {
                    vo.getVedioUrl().add(res.getUrl());
                }
            }
        }
    }

    @RequestMapping(value = "/updateSysnStatus", method = RequestMethod.POST)
    @ApiOperation(value = "updateSysnStatus", notes = "更新工序的同步状态")
    public Result<JSONObject> updateSysnStatus(@RequestParam(name = "craftCode", required = true) String craftCode,
                                               @RequestParam(name = "randomCode", required = true) Long randomCode,
                                               @RequestParam(name = "synStatus", required = true) Integer synStatus) {
        if (sewingcraftTmpService.updateSysnStatus(randomCode, craftCode, synStatus)) {
            JSONObject obj = new JSONObject();
            obj.put("randomCode", randomCode);
            obj.put("status", synStatus);
            return Result.ok(MessageConstant.SUCCESS, obj);
        }
        return Result.ok(MessageConstant.SERVER_FIELD_ERROR, new JSONObject());
    }

    /**
     * 传入多个工序编码进行查询,目前暂定大货款式工艺使用
     * 里面有一步是重新计算标准时间和标准单价，面料等级，面料系数，订单等级
     */
    @RequestMapping(value = "/getSewingCraftByCraftCodeList", method = RequestMethod.POST)
    @ApiOperation(value = "getSewingCraftByCraftCodeList", notes = "传入多个工序编码进行查询")
    public Result<List<JSONObject>> getSewingCraftByCraftCodeList(HttpServletRequest request) throws Exception {
        return null;
    }
}
