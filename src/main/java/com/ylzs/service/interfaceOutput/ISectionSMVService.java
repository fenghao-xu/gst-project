package com.ylzs.service.interfaceOutput;

import java.math.BigDecimal;

/**
 * 发送工段工时接口
 */
public interface ISectionSMVService {
    /**
     * @param styleCode 大货款号
     * @param isSend 是否发送
     * @param preSMV 预估工时
     * @return 发送的数据
     */
    String sendBigStylePreSectionSMV(String styleCode, BigDecimal preSMV, boolean isSend);

    /**
     * @param bigStyleAnalyseCode 大货款分析编码
     * @param isSend 是否发送
     * @return
     */
    String sendBigStyleActualSectionSMV(String bigStyleAnalyseCode, boolean isSend);

    String sendSectionSMV(String dataString);

    String sendCustomStyleActualSectionSMV(String orderNo, String orderLineId, boolean isSend);

    /**
     * @param productionTicketNo 工单号
     * @param isSend 是否发送
     * @return
     */
    String sendBigOrderActualSectionSMV(String productionTicketNo, boolean isSend);
}
