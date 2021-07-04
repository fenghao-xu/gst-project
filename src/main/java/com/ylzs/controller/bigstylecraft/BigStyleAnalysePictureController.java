package com.ylzs.controller.bigstylecraft;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.FastDFSUtil;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.controller.sewingcraft.SewingCraftWarehouseController;
import com.ylzs.entity.partCraft.PartCraftMasterPicture;
import com.ylzs.vo.partCraft.PartCraftMasterPictureVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ylzs.common.util.Assert.notNull;

/**
 * @author xuwei
 * @create 2020-03-24 11:20
 * 大货款式分析上传图片
 */
@RestController
@RequestMapping("/bigStyleAnalysePic")
@Api(tags = "大货款式工艺图片上传")
public class BigStyleAnalysePictureController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BigStyleAnalysePictureController.class);

    /**
     * 部件图片上传
     */
    @RequestMapping(value = "/uploadPicture", method = RequestMethod.POST)
    @ApiOperation(value = "/uploadPicture", notes = "上传款式工艺图片", response = Result.class)
    public Result<String> uploadPartCraftMasterPicture(@RequestParam(name = "file", required = true) MultipartFile file) throws Exception {
        notNull(file, "非法文件");
        String msg = "";
        try {
            String ext_Name = file.getOriginalFilename().split("\\.")[1];
            String url = FastDFSUtil.uploadFile(file.getBytes(), ext_Name);
            return Result.ok(MessageConstant.SUCCESS, url);
        } catch (Exception e) {
            msg = e.getMessage();
        }
        return Result.ok(MessageConstant.SERVER_FIELD_ERROR, msg);
    }

    @RequestMapping(value = "/getServerTime", method = RequestMethod.GET)
    @ApiOperation(value = "/getServerTime", notes = "获取服务器的时间", response = Result.class)
    public Result<String> getServerTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return Result.ok(MessageConstant.SUCCESS, sdf.format(new Date()));
    }

}
