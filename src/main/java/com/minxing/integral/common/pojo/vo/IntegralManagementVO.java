package com.minxing.integral.common.pojo.vo;

public class IntegralManagementVO {
    /**
     * id
     */
    private  Integer id;
    /**
     * 用户积分
     */
    private  Long integral;
    /**
     * 姓名
     */
    private String name;
    /**
     * 部门名称
     */
    private String shortName;
    /**
     * 勋值
     */
    private Long meritScore;
    /**
     * 兑换值
     */
    private Long value;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getMeritScore() {
        return meritScore;
    }

    public void setMeritScore(Long meritScore) {
        this.meritScore = meritScore;
    }
}
