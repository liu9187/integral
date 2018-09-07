package com.minxing.integral.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.minxing.integral.common.bean.Person;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class IntegralHandler implements EventHandler<Person>,WorkHandler<Person> {
    Logger logger = LoggerFactory.getLogger( IntegralHandler.class );
    @Override
    public void onEvent(Person event, long sequence, boolean endOfBatch) throws Exception {
         this.onEvent( event );
    }

    @Override
    public void onEvent(Person person) throws Exception {
        //赋值
        String domain=person.getDomain();
        String auth=person.getAuth();
        List<NameValuePair> urlParameters= person.getUrlParameters();
        logger.info("domain----"+domain+"-----auth------"+auth );
        //调用
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        JSONObject jsonObject = new JSONObject();
        HttpPut put = new HttpPut( domain+"/api/v1/user_infos/scores_direct");
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
        //返回对象
        //转换
        Integer code=(Integer)jsonObject.get( "code" );
        if (jsonObject.toString() == null) {
            logger.error( "Ruby interface call failed1 code:" + jsonObject.toString() );
        } else {
            //判断外部接口是否调用成功
            if (code != 200) {
                logger.warn( "Ruby interface call failed2 code:" + code );
            }
        }
        logger.info( "code:======="+ code);

    }
}
