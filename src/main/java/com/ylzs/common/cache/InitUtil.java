package com.ylzs.common.cache;

import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.system.WebConfig;
import com.ylzs.entity.timeparam.MotionCodeConfig;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.system.IWebConfigService;
import com.ylzs.service.timeparam.MotionCodeConfigService;
import com.ylzs.vo.DictionaryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2019-08-16 14:10
 */
public class InitUtil {
    private static Logger logger = LoggerFactory.getLogger(InitUtil.class);

    public static ApplicationContext applicationContext;

    public static void staticDataCacheInit(ApplicationContext ac) {
        IWebConfigService webConfigService = (IWebConfigService) ac.getBean("webConfigService");
        List<WebConfig> webConfigs = webConfigService.getConfigList();
        if (null != webConfigs && webConfigs.size() > 0) {
            for (WebConfig config : webConfigs) {
                StaticDataCache.STATIC_DATA_MAP.put(config.getKey(), config.getValue());
            }
        }
        MotionCodeConfigService motionCodeConfigService = (MotionCodeConfigService) ac.getBean("motionCodeConfigService");
        List<MotionCodeConfig> motionCodeConfigs = motionCodeConfigService.getAllMotionConfigs(null);
        if (null != motionCodeConfigs && motionCodeConfigs.size() > 0) {
            for (MotionCodeConfig config : motionCodeConfigs) {
                StaticDataCache.MOTIONS_DATA.put(config.getMotionCode(), config);
            }
        }
        IDictionaryService dictionaryService = (IDictionaryService) ac.getBean("dictionaryService");
        List<DictionaryVo> stopTimeList = dictionaryService.getDictoryAll(BusinessConstants.SewingParam.STOP_TIME);
        List<DictionaryVo> xianJiRationList = dictionaryService.getDictoryAll(BusinessConstants.SewingParam.XIAN_JI_RATION);
        List<DictionaryVo> workTypeList = dictionaryService.getDictoryAll(BusinessConstants.SewingParam.WORK_TYPE);
        if (null != stopTimeList && stopTimeList.size() > 0) {
            for (DictionaryVo vo : stopTimeList) {
                StaticDataCache.SEWING_PARAM_MAP.put(BusinessConstants.SewingParam.STOP_TIME + vo.getValueDesc(), new BigDecimal(vo.getDicValue()));
                StaticDataCache.SEWING_DICTIONARY_MAP.put(BusinessConstants.SewingParam.STOP_TIME + vo.getValueDesc(), vo);
            }
        }
        if (null != xianJiRationList && xianJiRationList.size() > 0) {
            for (DictionaryVo vo : xianJiRationList) {
                StaticDataCache.SEWING_PARAM_MAP.put(BusinessConstants.SewingParam.XIAN_JI_RATION + vo.getValueDesc(), new BigDecimal(vo.getDicValue()));
                StaticDataCache.SEWING_DICTIONARY_MAP.put(BusinessConstants.SewingParam.XIAN_JI_RATION + vo.getValueDesc(), vo);
            }
        }
        if (null != workTypeList && workTypeList.size() > 0) {
            for (DictionaryVo vo : workTypeList) {
                StaticDataCache.WORK_TYPE_DICTIONARY_MAP.put(vo.getDicValue().trim(), vo);
            }
        }
    }
}
