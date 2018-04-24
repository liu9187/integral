package com.minxing.integral.common.bean;

/**
 * 用户积分表
 * @author liucl
 * @date 2018-4-17
 */
public class UserIntegral {
 private  Integer id;
    /**
     * 用户id
     */
 private  Integer userId;
    /**
     * 用户积分
     */
 private  Long integral;
    /**
     * 积分id
     */

 private  Integer integralId;
    /**
     * 阅读量
     */
 private Long readingAmount;
    /**
     * 转发量
     *
     */
 private Long forwarding_amount;
    /**
     * 评论量
     */
 private Long comment_amount;

    public Long getReadingAmount() {
        return readingAmount;
    }

    public void setReadingAmount(Long readingAmount) {
        this.readingAmount = readingAmount;
    }

    public Long getForwarding_amount() {
        return forwarding_amount;
    }

    public void setForwarding_amount(Long forwarding_amount) {
        this.forwarding_amount = forwarding_amount;
    }

    public Long getComment_amount() {
        return comment_amount;
    }

    public void setComment_amount(Long comment_amount) {
        this.comment_amount = comment_amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getIntegralId() {
        return integralId;
    }

    public void setIntegralId(Integer integralId) {
        this.integralId = integralId;
    }
}
