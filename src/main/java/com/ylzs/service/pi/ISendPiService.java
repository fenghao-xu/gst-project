package com.ylzs.service.pi;

import org.apache.commons.lang3.tuple.Triple;

/**
 * PI发送接口
 */
public interface ISendPiService {
    /**
     * @param urlParam URL配置参数
     * @param dataObj 数据对象
     * @return 接口返回 left 提交返回 middle 出错信息 right 提交字串
     */
    Triple<String, String, String> postObject(String urlParam, Object dataObj);

    String sendToPi(String urlParam, Object data);

    String getPIJSON(String interfaceType, Object requestObj, String action);

    String sendToPi(String urlParam, String dataStr);

    String sendToPiRaw(String url, String appKey, String secretKey, String dataStr);

}
