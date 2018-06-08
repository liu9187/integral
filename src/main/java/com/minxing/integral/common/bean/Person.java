package com.minxing.integral.common.bean;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 *生产者和消费者操作数据
 */
public class Person {
    //传入参数
    private List<NameValuePair> urlParameters;
    //token
    private  String auth;
    //地址信息
    private  String domain;
     //无参构造器
    public Person() {
    }
      //有参数构造器
    public Person(List<NameValuePair> urlParameters, String auth, String domain) {
        this.urlParameters = urlParameters;
        this.auth = auth;
        this.domain = domain;
    }

    public List<NameValuePair> getUrlParameters() {
        return urlParameters;
    }

    public void setUrlParameters(List<NameValuePair> urlParameters) {
        this.urlParameters = urlParameters;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
