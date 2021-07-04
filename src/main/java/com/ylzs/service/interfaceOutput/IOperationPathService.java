package com.ylzs.service.interfaceOutput;

/**
 * 发送工艺路径接口
 */
public interface IOperationPathService {
    /**
     * @param orderNo 订制订单号
     * @param isSend 是否发送
     * @return null 失败 其他成功
     */
    String sendCustomStylePath(String orderNo, String lineId, String version, boolean isSend);

    /**
     * @param styleAnalyseCode 大货款款式分析号
     * @param isSend 是否发送
     * @return null 失败 其他成功
     */
    String sendBigStylePath(String styleAnalyseCode, boolean isSend);

    /**
     * @param productionTicketNo 生产工单号
     * @param isSend 是否发送
     * @return null 失败 其他成功
     */
    String sendBigOrderPath(String productionTicketNo, boolean isSend);
}
