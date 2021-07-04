package com.ylzs.controller.timeparam;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.entity.timeparam.MotionCodeConfig;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.timeparam.MotionCodeConfigService;
import com.ylzs.vo.timeparam.MotionTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-16 16:01
 * 动作代码定义
 */
@RestController
@RequestMapping("/motion")
@Api(tags = "动作代码定义")
public class MotionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotionController.class);
    @Resource
    private MotionCodeConfigService motionCodeConfigService;
    @Resource
    private IDictionaryService dictionaryService;

    private final BigDecimal DEFAULT_FREQUENCY = BigDecimal.ZERO;

    private final Integer ZERO = 0;

    // @Autowired
    // private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping(value = "/getMotionType", method = RequestMethod.GET)
    @ApiOperation(value = "getMotionType", notes = "获取动作类型")
    public Result<List<MotionTypeVo>> getMotionType() {
        List<MotionTypeVo> typeList = motionCodeConfigService.getMotionType();
        return Result.ok(MessageConstant.SUCCESS, typeList);
    }

    @RequestMapping(value = "/getMotionByCode", method = RequestMethod.GET)
    @ApiOperation(value = "getMotionByCode", notes = "通过动作代码查询")
    public Result<List<MotionCodeConfig>> getMotionByCode(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "30") Integer rows,
                                                          @RequestParam(required = false) String motionCode,
                                                          @RequestParam(required = false) String motionType,
                                                          @RequestParam(required = false) BigDecimal rpm,
                                                          @RequestParam(required = false) BigDecimal stitchLength) {
        LOGGER.info("查询的动作代码是：" + motionCode);
        if (StringUtils.isEmpty(motionCode)) {
            LOGGER.info("查询的动作代码是空，查询所有的");
            PageHelper.startPage(page, rows);
            List<MotionCodeConfig> motionCodeConfigs = motionCodeConfigService.getAllMotionConfigs(motionType);
            PageInfo<MotionCodeConfig> pageInfo = new PageInfo<>(motionCodeConfigs);
            return Result.build(MessageConstant.SUCCESS,
                    MessageConstant.messageMap.get(MessageConstant.SUCCESS),
                    new Long(pageInfo.getTotal()), motionCodeConfigs);
        }
        List<MotionCodeConfig> result = new ArrayList<>(0);
        motionCode = motionCode.toUpperCase();
        try {
            MotionCodeConfig motionCodeConfig = new MotionCodeConfig();
            if (motionCode.startsWith("Y")) {
//                String json = (String) redisTemplate.opsForHash().get("motion:code", "Y0000");
//                if (StringUtils.isNotEmpty(json)) {
//                    motionCodeConfig = JSONObject.parseObject(json, MotionCodeConfig.class);
//                }
                motionCodeConfig = StaticDataCache.MOTIONS_DATA.get("Y0000");
                if (null == motionCodeConfig) {
                    List<MotionCodeConfig> motions = motionCodeConfigService.getMotionByCode("Y0000", null);
                    if (motions != null && motions.size() > 0) {
                        motionCodeConfig = motions.get(0);
                        StaticDataCache.MOTIONS_DATA.put("Y0000", motions.get(0));
                    }
                }
                motionCodeConfig.setMotionCode(motionCode);
                motionCodeConfig.setMachineTime(Integer.parseInt(motionCode.substring(1)));
                motionCodeConfig.setManualTime(ZERO);
                motionCodeConfig.setFrequency(DEFAULT_FREQUENCY);
                result.add(motionCodeConfig);
            }
            // //特殊动作代码Z0000,的人工时间，等于去掉Z后后面的数字
            else if (motionCode.startsWith("Z")) {
//                String json = (String) redisTemplate.opsForHash().get("motion:code", "Z0000");
//                if (StringUtils.isNotEmpty(json)) {
//                    motionCodeConfig = JSONObject.parseObject(json, MotionCodeConfig.class);
//                }
                motionCodeConfig = StaticDataCache.MOTIONS_DATA.get("Z0000");
                if (null == motionCodeConfig) {
                    List<MotionCodeConfig> motions = motionCodeConfigService.getMotionByCode("Z0000", null);
                    if (motions != null && motions.size() > 0) {
                        motionCodeConfig = motions.get(0);
                        StaticDataCache.MOTIONS_DATA.put("Z0000", motions.get(0));
                    }
                }
                motionCodeConfig.setMotionCode(motionCode);
                motionCodeConfig.setMachineTime(ZERO);
                motionCodeConfig.setManualTime(Integer.parseInt(motionCode.substring(1)));
                motionCodeConfig.setFrequency(DEFAULT_FREQUENCY);
                result.add(motionCodeConfig);
            } else if (motionCode.equals("MXXXX")) {
                motionCodeConfig.setMotionCode(motionCode);
                motionCodeConfig.setMachineTime(ZERO);
                motionCodeConfig.setManualTime(ZERO);
                motionCodeConfig.setFrequency(DEFAULT_FREQUENCY);
                result.add(motionCodeConfig);
            } else if (motionCode.startsWith("M") && !motionCode.equals("MXXXX")) {
                StringBuffer sb = new StringBuffer();
                sb.append("车缝");
                if (!NumberUtil.isInteger(motionCode.substring(1, 3))) {
                    return Result.ok(result, new Long(result.size()));
                }
                sb.append(motionCode.substring(1, 3) + "CM");

                String xianji = motionCode.substring(3, 4);
                String stopTime = motionCode.substring(4);
                if (StaticDataCache.SEWING_DICTIONARY_MAP.containsKey(BusinessConstants.SewingParam.XIAN_JI_RATION + xianji)) {
                    sb.append(StaticDataCache.SEWING_DICTIONARY_MAP.get(BusinessConstants.SewingParam.XIAN_JI_RATION + xianji).getRemark());
                } else {
                    return Result.ok(result, new Long(result.size()));
                }
                if (StaticDataCache.SEWING_DICTIONARY_MAP.containsKey(BusinessConstants.SewingParam.STOP_TIME + stopTime)) {
                    sb.append("," + StaticDataCache.SEWING_DICTIONARY_MAP.get(BusinessConstants.SewingParam.STOP_TIME + stopTime).getRemark());
                } else {
                    return Result.ok(result, new Long(result.size()));
                }
                motionCodeConfig.setMotionName(sb.toString());
                motionCodeConfig.setMotionCode(motionCode);
                motionCodeConfig.setMachineTime(calMachineTime(motionCode, rpm, stitchLength));
                motionCodeConfig.setManualTime(ZERO);
                motionCodeConfig.setFrequency(DEFAULT_FREQUENCY);
                result.add(motionCodeConfig);
            } else {
                // String json = (String) redisTemplate.opsForHash().get("motion:code", motionCode);
                // redisTemplate.opsForHash().scan()
                if (StaticDataCache.MOTIONS_DATA.containsKey(motionCode)) {
                    result.add(StaticDataCache.MOTIONS_DATA.get(motionCode));
                } else {
                    List<MotionCodeConfig> motions = motionCodeConfigService.getMotionByCode(motionCode, motionType);
                    if (null != motions && motions.size() > 0) {
                        for (MotionCodeConfig config : motions) {
                            StaticDataCache.MOTIONS_DATA.put(config.getMotionCode(), config);
                        }
                    }
                    result.addAll(motions);
                }
            }

        } catch (Exception e) {
            LOGGER.error("查询的动作出现异常:" + e.getMessage());
        }
        return Result.ok(result, new Long(result.size()));
    }

    /**
     * 计算每一个动作 里面的机器时间
     */
    private Integer calMachineTime(String motionCode, BigDecimal rpm, BigDecimal stitchLength) {

        LOGGER.info("动作编码是:" + motionCode + " 转速是：" + rpm + " 针距是: " + stitchLength);
        //动作代码定义中的MXXXX为车缝动作代码，机器时间为0
        if ("MXXXX".equals(motionCode) || motionCode.indexOf("=") != -1) {
            return 0;
        }
        try {
            if (motionCode.startsWith("Y")) {
                return Integer.parseInt(motionCode.substring(1));
            } else if (motionCode.startsWith("M")) {
                //（ST/CM）每厘米的针数--stitchLength
                //MST（最小车缝时间）=（（ST/CM）/RPM）*1667
                BigDecimal mst = stitchLength.divide(rpm, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(1667)).setScale(6, BigDecimal.ROUND_HALF_UP);
                LOGGER.info("算出来的MST（最小车缝时间）：" + mst);
                //ROF(送布速度)=RPM/（ST/CM）
                BigDecimal rof = rpm.divide(stitchLength, 6, BigDecimal.ROUND_HALF_UP);
                LOGGER.info("算出来的ROF(送布速度)：" + rof);
                //超高速因素,默认设置为0，然后根据rof的情况来计算，
                // 当ROF<445时，HSF=1，HSF（超高速因素）=（4.5-MST）²/100+1 当ROF>445时
                BigDecimal hsf = new BigDecimal(0);
                if (rof.compareTo(new BigDecimal(445)) < 0) {
                    hsf = new BigDecimal(1);
                } else {
                    BigDecimal temp = new BigDecimal(4.5).subtract(mst);
                    hsf = temp.multiply(temp).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(1));
                }
                LOGGER.info("算出来的HSF(超高速因素)：" + hsf);
                //取末尾，标识停车代码
                int length = motionCode.length();
                BigDecimal p = StaticDataCache.SEWING_PARAM_MAP.get(BusinessConstants.SewingParam.STOP_TIME + motionCode.substring(length - 1));
                BigDecimal gt = StaticDataCache.SEWING_PARAM_MAP.get(BusinessConstants.SewingParam.XIAN_JI_RATION + motionCode.substring(length - 2, length - 1));
                BigDecimal cm = new BigDecimal(motionCode.substring(1, 3));
                LOGGER.info("p:" + p + " gt: " + gt + " cm: " + cm);
                BigDecimal result = mst.multiply(hsf).multiply(gt).multiply(cm).add(new BigDecimal(17)).add(p).setScale(0, BigDecimal.ROUND_HALF_UP);
                return result.intValue();
            }
        } catch (Exception e) {
            LOGGER.error("算人工时间出现异常:" + e.getMessage());
        }
        return 0;

    }
}
