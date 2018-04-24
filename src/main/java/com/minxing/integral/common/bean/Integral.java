package com.minxing.integral.common.bean;

/**
 * 积分规则表
 * @author liucl
 * @Date 2018-4-17
 */
public class Integral {
    private  Integer integralId;
    /**
     * 阅读积分
     */
    private  Integer readingIntegral;
    /**
     * 评论积分
     */
    private  Integer commentaryIntegral;
    /**
     * 转发积分
     */
    private  Integer forwardIntegral;
    /**
     * 积分设置
     */
    private  Integer integralModification;

    public Integer getIntegralModification() {
        return integralModification;
    }

    public void setIntegralModification(Integer integralModification) {
        this.integralModification = integralModification;
    }

    public Integer getIntegralId() {
        return integralId;
    }

    public void setIntegralId(Integer integralId) {
        this.integralId = integralId;
    }

    public Integer getReadingIntegral() {
        return readingIntegral;
    }

    public void setReadingIntegral(Integer readingIntegral) {
        this.readingIntegral = readingIntegral;
    }

    public Integer getCommentaryIntegral() {
        return commentaryIntegral;
    }

    public void setCommentaryIntegral(Integer commentaryIntegral) {
        this.commentaryIntegral = commentaryIntegral;
    }

    public Integer getForwardIntegral() {
        return forwardIntegral;
    }

    public void setForwardIntegral(Integer forwardIntegral) {
        this.forwardIntegral = forwardIntegral;
    }
}
