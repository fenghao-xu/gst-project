package com.ylzs.service.partCombCraft;

import com.ylzs.common.util.Result;
import com.ylzs.entity.partCombCraft.PartCombCraft;
import com.ylzs.entity.partCombCraft.req.PartCombCraftReq;
import com.ylzs.entity.partCombCraft.req.QueryPartCombCraftReq;
import com.ylzs.entity.partCombCraft.req.UpdatePartCombCraftReq;
import com.ylzs.entity.partCombCraft.resp.PartCombCraftAllDataResp;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * @author weikang
 * @Description
 * @Date 2020/3/12
 */
public interface IPartCombCraftService extends IOriginService<PartCombCraft> {

    /**
     * 发布、保存草稿
     * @param req
     * @return
     */
    Result addPartCombCraft(PartCombCraftReq req);

    /**
     * 1.删除草稿数据
     * 2.发布修改为失效
     * @param req
     * @return
     */
    Result updatePartCombCraft(UpdatePartCombCraftReq req);

    /**
     * 查询部件组合工艺清单
     * @param craftReq
     * @return
     */
    List<PartCombCraft> selectPartCombCraftChecklist(QueryPartCombCraftReq craftReq);

    /**
     * 获取清单页面部件组合工艺组合
     * @param randomCode
     * @return
     */
    Result selectPartCombCraftProgramDetail(String randomCode);

    /**
     * 获取部件组合工艺数据
     * @param randomCode
     * @return
     */
    Result selectPartCombCraftData(String randomCode);

    /**
     * 获取创建人
     * @return
     */
    Result selectPartCreateUser();

    /**
     * 获取部件组合工艺数据
     * @param clothingCategoryCode 服装品类编码
     * @param codeArray 设计部件编码
     * @return
     */
    List<PartCombCraftAllDataResp> selectPartDataByCategoryCode(String clothingCategoryCode,String[] codeArray);
}
