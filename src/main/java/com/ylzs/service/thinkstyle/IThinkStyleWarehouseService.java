package com.ylzs.service.thinkstyle;

import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.entity.thinkstyle.ThinkStyleWarehouse;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.thinkstyle.*;

import java.util.Date;
import java.util.List;

/**
 * @description: 智库款工艺
 * @author: lyq
 * @date: 2020-03-05 17:39
 */
public interface IThinkStyleWarehouseService extends IOriginService<ThinkStyleWarehouse> {
    List<ThinkStyleWarehouse> selectAllThinkStyle(String keywords, String[] craftCategoryCodes, String[] clothesCategoryCodes,
                                                        Date updateDateStart, Date updateDateStop, Integer status, Boolean isInvalid);
    /**获取整个智库款信息
     * @param styleCode 智库款款号
     * @return 智库款主数据，部件，工序和处理规则 （不包含动作)
     */
    ThinkStyleWarehouse getThinkStyleInfo(String styleCode);

    /**保存智库款
     * @param style 智库款
     * @return 影响行数
     */
    Integer updateThinkStyleWarehouse(ThinkStyleWarehouse style);

    /**获取智库款部件信息
     * @param part 智库款部件
     * @param thinkStyleCraftVos 智库款工艺
     * @param thinkStyleProcessRuleVos 智库款规则
     */
    void getThinkStylePartInfo(ThinkStyleWarehouse style, ThinkStylePart part, List<ThinkStyleCraftVo> thinkStyleCraftVos, List<ThinkStyleProcessRuleVo> thinkStyleProcessRuleVos);

    /**获取历史版本记录
     * @param randomCode 智库款关联代码
     * @return 历史版本记录
     */
    List<ThinkStyleHistoryVo> getThinkStyleHistoryVos(Long randomCode);


    /**查询是否可以发布
     * @param style 智库款对象
     * @return 返回null表示通过检查 否则返回失败信息
     */
    String checkPublish(ThinkStyleWarehouse style);

    /**添加修改智库款数据
     * @param styleCode 智库款代码
     * return 成功返回智库款的randomCode 失败返回0
     */
    long addOrUpdateProductModelDataDao(String styleCode);


    /**
     * @return 工艺品类和关联服装品类信息
     */
    List<CraftCategoryVo> getCraftCategoryVos();

    ThinkStyleWarehouseListVo getThinkStyleWarehouseListVo(ThinkStyleWarehouse obj);

    ThinkStyleWarehouseViewVo getThinkStyleWarehouseViewVo(ThinkStyleWarehouse obj);

    ThinkStyleWarehouse getThinkStyleWarehouse(long randomCode);


    void sendThinkStylePublishInfo(long randomCode);






}
