package com.minxing.integral.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class License {

    @Value("${mx.domain}")
    private String domainUrl;

    static Logger logger = LoggerFactory.getLogger(License.class);

    static String licenseBaseUrl;

    static String license;

    private static JSONObject licenseObj;

    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public License(@Value("${mx.domain}") String domainUrl) {
        this.domainUrl = domainUrl;
        try {
            licenseBaseUrl = domainUrl;
            String licenseStr = getLicense();
            String _licenseStr = decode(licenseStr);
            licenseObj = JSON.parseObject(_licenseStr);
        } catch (Exception e) {
            logger.error("License init error:>>", e);
        }
    }

    public JSONObject getLicenseObj() {
        if (null == licenseObj) {
            try {
                licenseBaseUrl = domainUrl;
                String licenseStr = getLicense();
                String _licenseStr = decode(licenseStr);
                licenseObj = JSON.parseObject(_licenseStr);
            } catch (Exception e) {
                logger.error("License getLicenseObj error:>>", e);
            }
        }
        return licenseObj;
    }

    private String getLicense() {
        HttpGet httpGet = new HttpGet(licenseBaseUrl + "/api/v1/kms/expire_date");
        try {
            HttpEntity entity = httpClient.execute(httpGet).getEntity();
            String s = EntityUtils.toString(entity);
            logger.info("getLicense>>>" + s);
            JSONObject hashMap = JSON.parseObject(s);
            license = hashMap.getString("data");
            logger.info("getLicense->data>>>" + license);
        } catch (IOException e) {
            logger.error("getLicense error:>>", e);
        }
        return license;
    }

    private static String decode(String str) {
        logger.info("decode>>>" + str);
        String a = AesHelper.base64decode(str);
        String hash = AesHelper.decrypt(AesHelper.hexStr2Byte(a), null, null);
        logger.info("decoded>>>" + hash);
        return hash;
    }

    public Boolean checkLicenseKey(String licenseKey) {
        JSONObject resultmap = this.getLicenseObj();
        if (resultmap == null) {
            //"产品License不支持文档共享，请联系管理员"
            logger.info("checkLicenseKey>>>产品License不支持该功能[" + licenseKey + "]，请联系管理员(10)");
            return false;
        }
        String date = resultmap.getString(licenseKey);
        if (date == null) {
            //"产品License不支持文档共享，请联系管理员"
            logger.info("checkLicenseKey>>>产品License不支持该功能[" + licenseKey + "]，请联系管理员(2)");
            return false;
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        long time = 0;
        try {
            time = f.parse(date).getTime();
        } catch (ParseException e) {
            logger.error("checkLicenseKey>>>checkLicense convert string to date error2:>>", e);
        }
        long licenseTime = 0;
        licenseTime = time + 24 * 60 * 60 * 1000;
        if (licenseTime < System.currentTimeMillis()) {
            logger.info("checkLicenseKey>>>产品License已过期，请联系管理员(2)");
            return false;
        }
        return true;
    }

    public static Boolean checkAutheyPermission(String url, HashMap<String, String> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        //建立HttpPost对象
        List<NameValuePair> post_params = new ArrayList<NameValuePair>();
        params.forEach((k, v) -> {
            post_params.add(new BasicNameValuePair(k, v));
        });
        httpPost.setEntity(new UrlEncodedFormEntity(post_params));
        try {
            HttpResponse response = httpClient.execute(httpPost);
            //发送Post,并返回一个HttpResponse对象
            return response.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

}
