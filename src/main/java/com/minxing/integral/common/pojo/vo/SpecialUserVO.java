package com.minxing.integral.common.pojo.vo;

/**
 * 特殊用户
 * @author liucl
 */
public class SpecialUserVO {
    /**
     * 姓名
     */
    private String name;
    /**
     *阅读数
     */
    private Long read;
    /**
     *评论数
     */
    private Long comment;
    /**
     *转发
     */
    private Long forward;
    /**
     * 评论+转发
     */
    private Long count1;
    /**
     *总合计
     */
    private Long count2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRead() {
        return read;
    }

    public void setRead(Long read) {
        this.read = read;
    }

    public Long getComment() {
        return comment;
    }

    public void setComment(Long comment) {
        this.comment = comment;
    }

    public Long getForward() {
        return forward;
    }

    public void setForward(Long forward) {
        this.forward = forward;
    }

    public Long getCount1() {
        return count1;
    }

    public void setCount1(Long count1) {
        this.count1 = count1;
    }

    public Long getCount2() {
        return count2;
    }

    public void setCount2(Long count2) {
        this.count2 = count2;
    }
}
