package com.ylzs.service.pi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.StringUtils;
import com.ylzs.dao.pi.CappPiSendpilogDao;
import com.ylzs.entity.pi.CappPiSendpilog;
import com.ylzs.service.pi.ISendPiService;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PI发送数据实现类
 */
@Service
public class SendPiService implements ISendPiService {
    @Resource
    private CappPiSendpilogDao cappPiSendpilogDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(SendPiService.class);

    @Override
    public Triple<String, String, String> postObject(String urlParam, Object dataObj) {
        String appKey = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.URL_AUTH_APP, "");
        String secretKey = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.URL_AUTH_SECRET, "");


        String url = StaticDataCache.getInstance().getValue(urlParam, "");
        String dataStr = JSON.toJSONString(dataObj,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero);
        String retMsg = "";
        String errMsg = "";
        dataStr = StringUtils.replaceSpecial(dataStr);
        try {
            HttpClientWithBasicAuth httpClient = new HttpClientWithBasicAuth(appKey, secretKey);
            retMsg = httpClient.PostData(url, dataStr);
        } catch (Exception e) {
            e.printStackTrace();
            errMsg = e.getMessage();
        }

        CappPiSendpilog cappPiSendpilog = new CappPiSendpilog();
        cappPiSendpilog.setSendType(urlParam);
        cappPiSendpilog.setDestUrl(url);
        cappPiSendpilog.setData(dataStr);
        cappPiSendpilog.setReturnMsg(retMsg);
        cappPiSendpilog.setFailMsg(errMsg);
        cappPiSendpilog.setCreateTime(new Date());
        cappPiSendpilogDao.insert(cappPiSendpilog);

        return new ImmutableTriple<>(retMsg, errMsg, dataStr);
    }

    @Override
    public String sendToPi(String urlParam, Object data) {
        String dataStr = getPIJSON(urlParam, data, null);
        String appKey = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.URL_AUTH_APP, "");
        String secretKey = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.URL_AUTH_SECRET, "");
        String url = StaticDataCache.getInstance().getValue(urlParam, "");
        String retMsg = "";
        String errMsg = "";
        try {
            HttpClientWithBasicAuth httpClient = new HttpClientWithBasicAuth(appKey, secretKey);
            dataStr = StringUtils.replaceSpecial(dataStr);
            retMsg = httpClient.PostData(url, dataStr);
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
        CappPiSendpilog cappPiSendpilog = new CappPiSendpilog();
        cappPiSendpilog.setSendType(urlParam);
        cappPiSendpilog.setDestUrl(url);
        cappPiSendpilog.setData(dataStr);
        cappPiSendpilog.setReturnMsg(retMsg);
        cappPiSendpilog.setFailMsg(errMsg);
        cappPiSendpilog.setCreateTime(new Date());
        cappPiSendpilogDao.insert(cappPiSendpilog);
        return dataStr;
    }

    @Override
    public String getPIJSON(String interfaceType, Object requestObj, String action) {
        DataParent<Object> parent = new DataParent<>();
        parent.setInterfaceType(interfaceType);
        parent.setSynTime(new Date());
        parent.setCount("1");
        List<DataChild<Object>> items = new ArrayList<>();
        DataChild<Object> item = new DataChild<>();
        item.setItemId("0");
        item.setItem(requestObj);
        if (StringUtils.isNotEmpty(action)) {
            item.setAction(action);
        }
        items.add(item);
        parent.setItems(items);
        return JSONObject.toJSONString(parent,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero);
    }

    @Override
    public String sendToPiRaw(String url, String appKey, String secretKey, String dataStr) {
        if (StringUtils.isEmpty(url) ||
                StringUtils.isEmpty(dataStr) ||
                StringUtils.isEmpty(appKey) ||
                StringUtils.isEmpty(secretKey)) {
            return null;
        }

        String retMsg = "";
        String errMsg = "";
        try {
            HttpClientWithBasicAuth httpClient = new HttpClientWithBasicAuth(appKey, secretKey);
            dataStr = StringUtils.replaceSpecial(dataStr);
            retMsg = httpClient.PostData(url, dataStr);
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
        CappPiSendpilog cappPiSendpilog = new CappPiSendpilog();
        cappPiSendpilog.setSendType(null);
        cappPiSendpilog.setDestUrl(url);
        cappPiSendpilog.setData(dataStr);
        cappPiSendpilog.setReturnMsg(retMsg);
        cappPiSendpilog.setFailMsg(errMsg);
        cappPiSendpilog.setCreateTime(new Date());
        cappPiSendpilogDao.insert(cappPiSendpilog);
        return retMsg;

    }

    @Override
    public String sendToPi(String urlParam, String dataStr) {
        String appKey = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.URL_AUTH_APP, "");
        String secretKey = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.URL_AUTH_SECRET, "");
        String url = StaticDataCache.getInstance().getValue(urlParam, "");
        String retMsg = "";
        String errMsg = "";
        try {
            HttpClientWithBasicAuth httpClient = new HttpClientWithBasicAuth(appKey, secretKey);
            dataStr = StringUtils.replaceSpecial(dataStr);
            retMsg = httpClient.PostData(url, dataStr);
        } catch (Exception e) {
            errMsg = e.getMessage();
        }
        CappPiSendpilog cappPiSendpilog = new CappPiSendpilog();
        cappPiSendpilog.setSendType(urlParam);
        cappPiSendpilog.setDestUrl(url);
        cappPiSendpilog.setData(dataStr);
        cappPiSendpilog.setReturnMsg(retMsg);
        cappPiSendpilog.setFailMsg(errMsg);
        cappPiSendpilog.setCreateTime(new Date());
        cappPiSendpilogDao.insert(cappPiSendpilog);
        return retMsg;
    }
}
