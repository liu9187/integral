package com.minxing.integral.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpNetClientUtil {
    Logger logger = LoggerFactory.getLogger( HttpNetClientUtil.class );

    /**
     * PUT基础请求
     *
     * @param urlParameters 提交参数
     * @return byte[] 请求成功后的结果
     */
    public static String doPut(List<NameValuePair> urlParameters, String auth,String domain) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        JSONObject jsonObject = new JSONObject();
        HttpPut put = new HttpPut( domain+"/api/v1/user_infos/scores");
        try {
            put.setHeader( "Authorization", "Bearer " + auth );
            put.setEntity( new UrlEncodedFormEntity( urlParameters, HTTP.UTF_8 ) );
            try {
                response = httpclient.execute( put );
                // logger.info( "状态" +response.getStatusLine().getStatusCode()  );
                // 判断网络连接状态码是否正常(0--200都数正常)
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    String content = EntityUtils.toString( response.getEntity(), "UTF-8" );
                    jsonObject = JSON.parseObject( content );
                    jsonObject.put( "message", "增加积分成功" );
                    jsonObject.put( "statusCode", response.getStatusLine().getStatusCode() );
                    //System.out.println( "报价返回内容是：" + jsonObject.toString() );
                }
                EntityUtils.consume( response.getEntity() );//完全消耗
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != response) response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            //释放链接
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public static void main(String[] args) {

        String Authorization = "d0qBA_gUd3PD-HkVzvX253xYCAUo4Q9uhM_wqCd_oPojM0u-";
        String data_type = "integral";
        String value = "1";
        String user_id = "1";
        String domain="http://dev8.dehuinet.com:8018";
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add( new BasicNameValuePair( "data_type", data_type ) );
        urlParameters.add( new BasicNameValuePair( "value", value ) );
        urlParameters.add( new BasicNameValuePair( "user_id", user_id ) );
        //urlParameters.add(new BasicNameValuePair("Authorization", Authorization));
        //HashMap<String, String> headers = new HashMap<>();
        String c = doPut( urlParameters, Authorization,domain);
        //  System.out.print(  c);
/***
 * {"code":200}
 */
    }
}



