package com.ylzs.service.partCraft;


import com.ylzs.entity.partCraft.PartCraftMasterPicture;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.partCraft.PartCraftMasterPictureVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 部件工艺主数据图片
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
public interface IPartCraftMasterPictureService extends IOriginService<PartCraftMasterPicture> {


    PartCraftMasterPicture getPartCraftPicture(Long randomCode);

    String saveOrUpdatePicture(MultipartFile orgFile)throws Exception;

    Boolean delPicture(String picUrl)throws Exception;

    List<PartCraftMasterPicture> getPartCraftPictureMainDataList(Long partCraftMainCode);

    List<PartCraftMasterPicture> getPartCraftPictureMainDataList(Long partCraftMainCode,Integer status);

    List<PartCraftMasterPictureVo>getPictureByPartCraftMainRandomCode(Long partCraftMainCode);

    List<PartCraftMasterPictureVo> getPartCraftPictureVoList(Long partCraftMainCode,Integer status);

    Map<Long,List<PartCraftMasterPictureVo>> getPictureGroupMainRandomCodeByList(List<Long> mainRandomCodes);
}

