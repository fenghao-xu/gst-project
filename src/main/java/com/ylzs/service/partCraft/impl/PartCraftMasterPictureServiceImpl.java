package com.ylzs.service.partCraft.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.util.FastDFSUtil;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.core.model.SuperEntity;
import com.ylzs.dao.partCraft.PartCraftMasterPictureDao;
import com.ylzs.entity.partCraft.PartCraftMasterPicture;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCraft.IPartCraftMasterPictureService;
import com.ylzs.vo.partCraft.PartCraftMasterPictureVo;
import org.csource.common.NameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service()
@Transactional()
public class PartCraftMasterPictureServiceImpl extends OriginServiceImpl<PartCraftMasterPictureDao, PartCraftMasterPicture> implements IPartCraftMasterPictureService {

    @Resource
    private PartCraftMasterPictureDao partCraftMasterPictureDao;

    @Override
    public PartCraftMasterPicture getPartCraftPicture(Long randomCode) {
        return partCraftMasterPictureDao.getPartCraftPicture(randomCode);
    }

    @Override
    public List<PartCraftMasterPictureVo> getPictureByPartCraftMainRandomCode(Long partCraftMainCode) {
        return partCraftMasterPictureDao.getPictureByPartCraftMainRandomCode(partCraftMainCode);
    }

    @Override
    public String saveOrUpdatePicture(MultipartFile file) throws Exception {
        String ext_Name = file.getOriginalFilename().split("\\.")[1];
        String file_Name = file.getOriginalFilename().split("\\.")[0];
        byte[] bytes = file.getBytes();
        NameValuePair[] nvps = new NameValuePair[1];
        nvps[0] = new NameValuePair(file_Name, ext_Name);
        return FastDFSUtil.uploadFile(bytes, ext_Name, nvps);
    }

    @Override
    public Boolean delPicture(String picUrl) throws Exception {
        return FastDFSUtil.deleteFileByUrl(picUrl);
    }

    @Override
    public List<PartCraftMasterPicture> getPartCraftPictureMainDataList(Long partCraftMainCode) {
        List<PartCraftMasterPicture> list = new ArrayList<>();
        try {
            list = partCraftMasterPictureDao.getPartCraftPictureMainDataList(partCraftMainCode);
        } catch (Exception ex) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftMasterPicture> getPartCraftPictureMainDataList(Long partCraftMainCode, Integer status) {
        List<PartCraftMasterPicture> list = new ArrayList<>();
        try {
            QueryWrapper<PartCraftMasterPicture> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(PartCraftMasterPicture::getPartCraftMainRandomCode, partCraftMainCode)
                    .eq(SuperEntity::getStatus, status);
            list = partCraftMasterPictureDao.selectList(queryWrapper);
        } catch (Exception ex) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<PartCraftMasterPictureVo> getPartCraftPictureVoList(Long partCraftMainCode, Integer status) {
        List<PartCraftMasterPictureVo> list = new ArrayList<>();
        try {
            list = partCraftMasterPictureDao.getPartCraftPictureVoList(partCraftMainCode, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<Long, List<PartCraftMasterPictureVo>> getPictureGroupMainRandomCodeByList(List<Long> mainRandomCodes) {
        Map<Long, List<PartCraftMasterPictureVo>> groupByMap = new HashMap<>();
        try {
            if (null != mainRandomCodes && mainRandomCodes.size() > 0) {
                List<PartCraftMasterPictureVo> list = partCraftMasterPictureDao.getPartCraftPictureBatchList(mainRandomCodes);
                if (ObjectUtils.isNotEmptyList(list)) {
                    groupByMap = list.stream().collect(Collectors.groupingBy(PartCraftMasterPictureVo::getPartCraftMainRandomCode));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return groupByMap;
    }
}