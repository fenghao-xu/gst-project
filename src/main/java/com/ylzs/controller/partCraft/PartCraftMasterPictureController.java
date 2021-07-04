package com.ylzs.controller.partCraft;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.util.FastDFSUtil;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.entity.partCraft.PartCraftMasterPicture;
import com.ylzs.service.partCraft.IPartCraftMasterDataService;
import com.ylzs.vo.partCraft.PartCraftMasterPictureVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.csource.common.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ylzs.service.partCraft.IPartCraftMasterPictureService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

import static com.ylzs.common.util.Assert.notNull;


/**
 * 部件工艺主数据图片
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@RestController
@RequestMapping(value = "/partCraftPicture")
public class PartCraftMasterPictureController {

    @Autowired
    private IPartCraftMasterPictureService partCraftMasterPictureService;
    @Resource
    private IPartCraftMasterDataService partCraftMasterDataService;

    /**
     * 部件图片上传
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/uploadPicture", method = RequestMethod.POST,headers = "content-type=multipart/form-data")
    @ApiOperation(value = "/uploadPicture", notes = "上传部件图片", response = Result.class)
    public Result<PartCraftMasterPictureVo> uploadPartCraftMasterPicture(HttpServletRequest request,
                                                                         @RequestParam(name = "file", required = true) MultipartFile file) throws Exception {
        notNull(file, "非法文件");
        String url = partCraftMasterPictureService.saveOrUpdatePicture(file);
        PartCraftMasterPicture masterPicture = new PartCraftMasterPicture();
        masterPicture.setRandomCode(SnowflakeIdUtil.generateId());
        masterPicture.setPictureUrl(url);
        masterPicture.setIsInvalid(false);
        boolean result = partCraftMasterPictureService.save(masterPicture);
        if (!result) {
            throw new SQLSyntaxErrorException();
        }
        PartCraftMasterPictureVo pictureVo = new PartCraftMasterPictureVo();
        pictureVo.setRandomCode(masterPicture.getRandomCode());
        pictureVo.setPictureUrl(url);
        return Result.ok(pictureVo);
    }

    @RequestMapping(value = "/updateUploadPartCraftPicture", method = RequestMethod.POST,headers = "content-type=multipart/form-data")
    @ApiOperation(value = "/updateUploadPartCraftPicture", notes = "更新或者删除图片", response = Result.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "picRandomCode",value = "需要更新或者修改图片的唯一识别码",dataType = "Long",required = false),
            @ApiImplicitParam(name = "actionType",value = "操作类型 0-更新 1-删除",dataType = "Integer",required = true),
            @ApiImplicitParam(name = "pictureUrl",value = "需要删除的部件图片URL地址",dataType = "String",required = false)})
    public Result<PartCraftMasterPictureVo> updateUploadPartCraftPicture(HttpServletRequest request,
                                                                         @RequestParam(name = "picRandomCode", required = false) Long randomCode,
                                                                         @RequestParam(name = "actionType", required = true) Integer actionType,
                                                                         @RequestParam(name = "pictureUrl", required = false) String pictureUrl,
                                                                         @RequestParam(name = "file", required = false) MultipartFile file) throws Exception {

        PartCraftMasterPicture masterPicture = partCraftMasterPictureService.getPartCraftPicture(randomCode);
        if (ObjectUtils.isEmpty(masterPicture)) {
            throw new RuntimeException();
        }
        if (actionType == 0) {
            String url = partCraftMasterPictureService.saveOrUpdatePicture(file);
            masterPicture.setPictureUrl(url);

        } else {
            masterPicture.setIsInvalid(true);
            partCraftMasterPictureService.delPicture(pictureUrl);
        }
        boolean updateResult = partCraftMasterPictureService.updateById(masterPicture);
        if (!updateResult) {
            throw new SQLSyntaxErrorException();
        }
        PartCraftMasterPictureVo vo = new PartCraftMasterPictureVo();
        vo.setPictureUrl(masterPicture.getPictureUrl());
        vo.setRandomCode(masterPicture.getRandomCode());
        return Result.ok(vo);
    }


}
