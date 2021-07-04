package com.ylzs.schedual;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.entity.materialCraft.MaterialCraft;
import com.ylzs.service.materialCraft.IMaterialCraftPropertyService;
import com.ylzs.service.materialCraft.IMaterialCraftRulePartService;
import com.ylzs.service.materialCraft.IMaterialCraftRuleService;
import com.ylzs.service.materialCraft.IMaterialCraftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author weikang
 * @Description 每晚凌晨零点检查更新材料工艺状态
 * @Date 2020/3/10
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class UpdateMaterialCraftStatuSchedual {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateMaterialCraftStatuSchedual.class);

    @Resource
    private IMaterialCraftService materialCraftService;

    @Resource
    private IMaterialCraftPropertyService propertyService;

    @Resource
    private IMaterialCraftRuleService ruleService;

    @Resource
    private IMaterialCraftRulePartService rulePartService;


    @Async
    @Scheduled(cron = "0 0 0 * * ? ")
//    @Scheduled(cron = "0 */3 * * * ? ")
    public void updateMaterialCraftStatus() throws Exception {
        LOGGER.info("updateMaterialCraftStatus start--->>>");

        List<Long> publishList = materialCraftService.selectPublishRandomCode();
        //更新材料工艺 生效的为失效
        if(CollUtil.isNotEmpty(publishList)){
            int uCraftNotActive = materialCraftService.updateNotActiveStatus(publishList);
            if (uCraftNotActive < BusinessConstants.CommonConstant.ONE) {
                LOGGER.info("updateMaterialCraftStatus uCraftNotActive fail--->>>");
                return;
            }
            int uPropertyNotActive = propertyService.updateNotActiveStatus(publishList);
            if (uPropertyNotActive < BusinessConstants.CommonConstant.ONE) {
                LOGGER.info("updateMaterialCraftStatus uPropertyNotActive fail--->>>");
                return;
            }
            int uRuleNotActive = ruleService.updateNotActiveStatus(publishList);
            if (uRuleNotActive < BusinessConstants.CommonConstant.ONE) {
                LOGGER.info("updateMaterialCraftStatus uRuleNotActive fail--->>>");
                return;
            }
            int countRulePart = rulePartService.selectCountByCraftRandomCodes(publishList);
            if(countRulePart>BusinessConstants.CommonConstant.ZERO){
                int uRulePartNotActive = rulePartService.updateNotActiveStatus(publishList);
                if (uRulePartNotActive < BusinessConstants.CommonConstant.ONE) {
                    LOGGER.info("updateMaterialCraftStatus uRulePartNotActive fail--->>>");
                    return;
                }
            }

        }

        List<Long> list = materialCraftService.selectRandomCode();
        //更新材料工艺 即将生效为生效
        if(CollUtil.isNotEmpty(list)){
            int uCraft = materialCraftService.updatePublishStatus();
            if (uCraft < BusinessConstants.CommonConstant.ONE) {
                LOGGER.info("updateMaterialCraftStatus uCraft fail--->>>");
                return;
            }
            int uProperty = propertyService.updatePublishStatus(list);
            if (uProperty < BusinessConstants.CommonConstant.ONE) {
                LOGGER.info("updateMaterialCraftStatus uProperty fail--->>>");
                return;
            }
            int uRule = ruleService.updatePublishStatus(list);
            if (uRule < BusinessConstants.CommonConstant.ONE) {
                LOGGER.info("updateMaterialCraftStatus uRule fail--->>>");
                return;
            }
            int countRulePart = rulePartService.selectCountByCraftRandomCodes(list);
            if(countRulePart>BusinessConstants.CommonConstant.ZERO){
                int uRulePart = rulePartService.updatePublishStatus(list);
                if (uRulePart < BusinessConstants.CommonConstant.ONE) {
                    LOGGER.info("updateMaterialCraftStatus uRulePart fail--->>>");
                    return;
                }
            }
        }
        LOGGER.info("updateMaterialCraftStatus end--->>>");
    }

}
