package com.ylzs.service.processCombCraft;

import com.ylzs.common.util.Result;
import com.ylzs.entity.processCombCraft.ProcessCombCraft;
import com.ylzs.entity.processCombCraft.req.ProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.req.QueryProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.req.UpdateProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.resp.ProcessCombCraftAllDataResp;
import com.ylzs.service.IOriginService;

import java.util.List;

/**
 * @author weikang
 * @Description 工序组合工艺
 * @Date 2020/3/14
 */
public interface IProcessCombCraftService extends IOriginService<ProcessCombCraft> {

    /**
     * 新增工序组合工艺
     * @param req
     * @return
     */
    Result addProcessCombCraft(ProcessCombCraftReq req);

    /**
     * 查询工序组合工艺数据
     * @param randomCode
     * @return
     */
    Result selectProcessCombCraftData(String randomCode);

    /**
     * 查询工序组合工艺方案数据
     * @param randomCode
     * @return
     */
    Result selectProcessCombCraftProgram(String randomCode);

    /**
     * 清单页面返回数据
     * @param craftReq
     * @return
     */
    List<ProcessCombCraft> selectProcessCombCraftChecklist(QueryProcessCombCraftReq craftReq);

    /**
     * 更新状态
     * @param req
     * @return
     */
    Result updateProcessCombCraft(UpdateProcessCombCraftReq req);

    /**
     * 获取创建人
     * @return
     */
    Result selectProcessCreateUser();

    /**
     * 获取工序组合工艺数据
     * @param clothingCategoryCode 服装品类编码
     * @return
     */
    List<ProcessCombCraftAllDataResp> selectProcessDataByCategoryCode(String clothingCategoryCode);
}
