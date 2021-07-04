package com.ylzs.service.materialCraft;

import com.ylzs.common.util.Result;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.entity.materialCraft.req.MaterialCraftReq;
import com.ylzs.entity.materialCraft.req.QueryMaterialCraftReq;
import com.ylzs.entity.materialCraft.resp.MaterialCraftAllDataResp;
import com.ylzs.entity.materialCraft.resp.QueryMaterialCraftDataResp;
import com.ylzs.entity.partCombCraft.req.UpdatePartCombCraftReq;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * @author weikang
 * @Description 材料工艺
 * @Date 2020/3/5
 */
public interface IMaterialCraftService extends IOriginService<MaterialCraft> {

    /**
     * 新增草稿
     * @param req
     * @return
     */
    Result addDraftMaterialCraft(MaterialCraftReq req);

    /**
     * 新增发布
     * @param req
     * @return
     */
    Result addPublishMaterialCraft(MaterialCraftReq req);


    /**
     * 修改材料工艺状态
     * @param req
     * @return
     */
    Result updateMaterialCraftStatus(UpdatePartCombCraftReq req);

    /**
     * 材料工艺清单
     * @param craftReq
     * @return
     */
    List<QueryMaterialCraftDataResp> selectMaterialCraftChecklist(QueryMaterialCraftReq craftReq);

    /**
     * 查询材料工艺和相关联数据
     * @param randomCode
     * @return
     */
    Result selectMaterialCraftData(String randomCode);

    /**
     * 更新查询材料工艺状态为生效
     * @return
     */
    int updatePublishStatus();

    /**
     * 查询即将生效的材料工艺randomcode
     * @return
     */
    List<Long> selectRandomCode();

    /**
     * 查询材料工艺规则部件
     * @param randomCode
     * @return
     */
    Result selectMaterialCraftRulePart(String randomCode);

    /**
     * 获取材料工艺数据
     * @param kindCode 材料类型编码
     * @return
     */
    List<MaterialCraftAllDataResp> selectMaterialCraftDataByKindCode(String kindCode);

    /**
     * 更新查询材料工艺状态为失效
     * @param list
     * @return
     */
    int updateNotActiveStatus(List<Long> list);

    /**
     * 查询生效的材料工艺randomcode
     * @return
     */
    List<Long> selectPublishRandomCode();

}
