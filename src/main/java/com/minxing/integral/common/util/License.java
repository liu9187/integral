package com.minxing.integral.common.util;

/**
 * 2018年5月10日
 * 增加license校验类
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
@Component
public class License {
    private static Logger logger = LoggerFactory.getLogger(License.class);

    //license 校验： license key
    @Value("${license.key}")
    private String LICENSE_KEY;
    //敏行接口地址，这个地址要在系统的hosts文件中做ip映射
   @Value("${license.base.url}")
    private String LICENSE_BASE_URL;

    private  String license;

    private  JSONObject licenseObj;

    private  CloseableHttpClient httpClient = HttpClients.createDefault();

    private  JSONObject getLicenseObj() {
        if (null == licenseObj) {
            try {
                String licenseStr = getLicense();
                String _licenseStr = decode(licenseStr);
                licenseObj = JSON.parseObject(_licenseStr);
            } catch (Exception e) {
                logger.error("License getLicenseObj error:>>", e);
            }
        }
        return licenseObj;
    }

    private  String getLicense() {
        HttpGet httpGet = new HttpGet(LICENSE_BASE_URL + "/api/v1/kms/expire_date");
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

    private  String decode(String str) {
        logger.info("decode>>>" + str);
        String a = AesHelper.base64decode(str);
        String hash = AesHelper.decrypt(AesHelper.hexStr2Byte(a), null, null);
        logger.info("decoded>>>" + hash);
        return hash;
    }

    public  Boolean checkLicense() {
//        if (!IS_CHECK) {
//            return true;
//        }
        JSONObject resultmap = getLicenseObj();
        if (resultmap == null) {
            //"产品License不支持文档共享，请联系管理员"
            logger.info("checkLicenseKey>>>产品License不支持该功能[" + LICENSE_KEY + "]，请联系管理员(1)");
            return false;
        }
        String date = resultmap.getString(LICENSE_KEY);
        if (date == null) {
            //"产品License不支持文档共享，请联系管理员"
            logger.info("checkLicenseKey>>>产品License不支持该功能[" + LICENSE_KEY + "]，请联系管理员(2)");
            return false;
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        long time = 0;
        try {
            time = f.parse(date).getTime();
        } catch (ParseException e) {
            logger.error("checkLicenseKey>>>checkLicense convert string to date error:>>", e);
        }
        long licenseTime = 0;
        licenseTime = time + 24 * 60 * 60 * 1000;
        if (licenseTime < System.currentTimeMillis()) {
            logger.info("checkLicenseKey>>>产品License已过期，请联系管理员(3)");
            return false;
        }
        return true;
    }

}
