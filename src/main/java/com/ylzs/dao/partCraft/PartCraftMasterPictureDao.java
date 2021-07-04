package com.ylzs.dao.partCraft;


import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.partCraft.PartCraftMasterPicture;
import com.ylzs.vo.partCraft.PartCraftMasterPictureVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部件工艺主数据图片
 * 
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 16:39:26
 */
@Mapper
public interface PartCraftMasterPictureDao extends BaseDAO<PartCraftMasterPicture> {

    @Select("SELECT * FROM part_craft_master_picture WHERE random_code = #{randomCode}")
    PartCraftMasterPicture getPartCraftPicture(Long randomCode);

    @Select("SELECT * FROM part_craft_master_picture WHERE part_craft_main_random_code = #{partCraftMainCode}")
    List<PartCraftMasterPicture> getPartCraftPictureMainDataList(Long partCraftMainCode);

    List<PartCraftMasterPictureVo>getPictureByPartCraftMainRandomCode(Long partCraftMainCode);

    @Select("SELECT * FROM part_craft_master_picture WHERE part_craft_main_random_code = #{partCraftMainCode} AND status = #{status}")
    List<PartCraftMasterPicture> getPartCraftPictureList(Long partCraftMainCode,Integer status);
    @Select("SELECT * FROM part_craft_master_picture WHERE part_craft_main_random_code = #{partCraftMainCode} AND status = #{status}")
    List<PartCraftMasterPictureVo> getPartCraftPictureVoList(Long partCraftMainCode,Integer status);

    List<PartCraftMasterPictureVo> getPartCraftPictureBatchList(@Param("mainRandomCodes") List<Long> mainRandomCodes);
}
