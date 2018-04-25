package com.minxing.integral.common.pojo.vo;

/**
 * 普通用户
 * @author liucl
 */
public class OrdinaryUserVO {
    /**
     * 姓名
     */
    private String name;
    /**
     * 阅读次数
     */
    private Long read;
    /**
     * 评论数
     */
    private Long comment;
    /**
     * 合计
     */
    private Long count;

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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
