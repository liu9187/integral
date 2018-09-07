package com.minxing.integral.common.bean;

/**
 * 用户列表
 */
public class UserInfos {
    private Integer userId;
    private Long integral;
    private Long meritScore;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public Long getMeritScore() {
        return meritScore;
    }

    public void setMeritScore(Long meritScore) {
        this.meritScore = meritScore;
    }
}
