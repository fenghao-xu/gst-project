package com.ylzs.common.util;

import com.ylzs.common.constant.RedisContants;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author xuwei
 * @create 2020-03-30 17:28
 */
public class CreateSerialUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SewingCraftWarehouseService.class);
    private static final String CONST_LOCK = ":lockkey";
    private static final String SERIALS = ":serials";

    /**
     * 产生序列号
     * module--业务功能模块，防止key重复冲突，就当有命名空间一样，可以自定义，例如工序管理就是sewing
     * key---编码组成是英文字母+流水号，这个key就是那些英文字母
     */
    public static String createSerial(RedisUtil redisUtil, RedisTemplate redisTemplate, String module, String key, int length, Integer defaultVaule) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        String serialNumber = "";
        boolean getLock = false;
        String requestId = UUIDUtil.uuid32();
        try {
            getLock = redisUtil.tryGetDistributedLock(module + key + CONST_LOCK, requestId, RedisContants.EXPIRE_TIME);
            int step = 0;
            //获取失败，继续循环获取
            while (!getLock) {
                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                    getLock = redisUtil.tryGetDistributedLock(module + key + CONST_LOCK, requestId, RedisContants.EXPIRE_TIME);
                    if (getLock || step > 100) {
                        //获取成功，就跳出循环
                        break;
                    }
                    step++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (getLock) {
                Object serialno = redisTemplate.opsForValue().get(module + key + SERIALS);
                String format = "%0" + length + "d";
                //第一个序列号生成
                if (null == serialno) {
                    if (null == defaultVaule) {
                        defaultVaule = 1;
                    }
                    serialNumber = String.format(format, defaultVaule);
                    redisTemplate.opsForValue().set(module + key + SERIALS, defaultVaule);
                } else {
                    //先把序列号加1；
                    redisTemplate.opsForValue().increment(module + key + SERIALS);
                    serialNumber = String.format(format, (Integer) redisTemplate.opsForValue().get(module + key + SERIALS));
                }
                LOGGER.info("---车缝工序词库编码生成的序列号是:" + serialNumber);
            } else {
                LOGGER.error("---" + module + key + ":码获取分布式锁失败!!!-----------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取分布式锁失败，" + e.getMessage());
        } finally {
            //一定要释放锁
            redisUtil.releaseDistributedLock(module + key + CONST_LOCK, requestId);
        }
        return serialNumber;
    }
}
