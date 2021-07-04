package com.ylzs.common.util;

import com.ylzs.controller.bigstylecraft.BigStyleAnalysePictureController;
import com.ylzs.entity.bigstylecraft.BigStyleOperationLog;
import com.ylzs.service.bigstylecraft.BigStyleOperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuwei
 * @create 2020-08-05 8:56
 */
public class LogUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);

    public static void insertBigStyleOperation(BigStyleOperationLogService bigStyleOperationLogService, String data, String type) {
        try {
            if (null == bigStyleOperationLogService) {
                return;
            }
            BigStyleOperationLog log = new BigStyleOperationLog();
            log.setReceiveData(data);
            log.setUpdateType(type);
            bigStyleOperationLogService.insertData(log);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void insertBigStyleOperationToLOG(String data, String type) {
        try {
            LOGGER.info(type + "----------" + data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
