package com.ylzs.service.interfaceOutput;

public interface INewCraftService {
    /**
     * @param styleAnalyseCode 大货款分析号
     * @param isSend 是否发送
     * @return
     */
    String sendBigStyleNewCraft(String styleAnalyseCode, boolean isSend);

    /**
     * @param productionTicketNo 工单号
     * @param isSend 是否发送
     * @return
     */
    String sendBigOrderNewCraft(String productionTicketNo, boolean isSend);
}
