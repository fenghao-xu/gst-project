package com.ylzs.service.pi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.util.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;


/**
 * Http 发送数据实现类
 */
public class HttpClientWithBasicAuth {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientWithBasicAuth.class);
    private String appKey;
    private String secretKey;

    public HttpClientWithBasicAuth(String appKey, String secretKey) {
        this.appKey = appKey;
        this.secretKey = secretKey;
    }

    /**
     * 构造Basic Auth认证头信息
     *
     * @return
     */
    private String getHeader() {
        String auth = appKey + ":" + secretKey;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }


    public String PostData(String url, String dataStr) throws Exception {

        if (StringUtils.isEmpty(url)) {
            throw new Exception("url地址不能为空");
        }

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);


        LOGGER.debug("要发送的数据:", url, dataStr);
        StringEntity myEntity = new StringEntity(dataStr, ContentType.APPLICATION_JSON); // 构造请求数据
        post.addHeader("Authorization", getHeader());
        //post.addHeader("Content-Type","application/json;charset=UTF-8");  
        post.setEntity(myEntity); // 设置请求体

        String responseContent = null; // 响应内容
        CloseableHttpResponse response = null;
        JSONObject json = null;

        try {
            response = client.execute(post);
            LOGGER.debug("接收到的数据：", JSON.toJSONString(response));
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
                //json = JSONObject.fromObject(responseContent);//解决中文乱码
                return responseContent != null ? responseContent : "";
            }

            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.close();
            }



        } catch (Exception e) {
            LOGGER.error("返回异常 ClientProtocolException", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return null;
    }


    public static String SendData(String url, String dataStr) throws Exception  {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        StringEntity myEntity = new StringEntity(dataStr, ContentType.APPLICATION_JSON); // 构造请求数据
        //post.addHeader("Content-Type","application/json;charset=UTF-8");
        post.setEntity(myEntity); // 设置请求体
        String responseContent = null;
        try {
            CloseableHttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
                //json = JSONObject.fromObject(responseContent);//解决中文乱码
                return responseContent != null ? responseContent : "";
            }

            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.close();
            }


        } catch (Exception e) {
            LOGGER.debug("出错信息：", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return null;
    }
}
