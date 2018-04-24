package com.minxing.integral.common.bean;

/**
 * 积分规则表
 * @author liucl
 * @Date 2018-4-17
 */
public class Integral {
    private Integer id;
    private String type;
    private Long integral ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }
}
