package com.ylzs.common.cache;

import com.ylzs.entity.timeparam.MotionCodeConfig;
import com.ylzs.service.craftstd.ICraftStdService;
import com.ylzs.vo.DictionaryVo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StaticDataCache {
    public static final Map<String, String> STATIC_DATA_MAP = new HashMap<String, String>();
    public static final Map<String, BigDecimal> SEWING_PARAM_MAP = new HashMap<String, BigDecimal>();
    public static final Map<String, DictionaryVo> SEWING_DICTIONARY_MAP = new HashMap<String, DictionaryVo>();
    public static final Map<String, DictionaryVo> WORK_TYPE_DICTIONARY_MAP = new HashMap<String, DictionaryVo>();

    public static final ConcurrentHashMap<String, MotionCodeConfig> MOTIONS_DATA = new ConcurrentHashMap<String, MotionCodeConfig>();

   // public static final ConcurrentHashMap<String, Object> TOKEN_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, List<ICraftStdService.ShareFileInfo>> PIC_FILE_INFO_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, List<ICraftStdService.ShareFileInfo>> MP4_FILE_INFO_MAP = new ConcurrentHashMap<>();

    private StaticDataCache() {

    }

    public static StaticDataCache getInstance() {
        return StaticDataCacheLoader.instance;
    }

    public String getValue(String key) {
        return getValue(key, null);
    }

   /* public Object getToken(String key) {
        return TOKEN_MAP.get(key);
    }*/

    public String getValue(String key, String defaultResult) {
        String result = STATIC_DATA_MAP.get(key);
        if (result == null) {
            return defaultResult;
        }
        return result;
    }

    private static class StaticDataCacheLoader {
        private static final StaticDataCache instance = new StaticDataCache();
    }
}
