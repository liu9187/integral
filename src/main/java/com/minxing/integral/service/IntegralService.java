package com.minxing.integral.service;

/**
 * @author SuZZ on 2018/4/27.
 */
public interface IntegralService {

    /**
     * 公众号阅读
     */
    String OCU_ARTICLE_READ = "OCU_ARTICLE_READ";
    /**
     * 公众号评论
     */
    String OCU_ARTICLE_COMMENT = "OCU_ARTICLE_COMMENT";
    /**
     * 公众号转发
     */
    String OCU_ARTICLE_FORWARD = "OCU_ARTICLE_FORWARD";

    /**
     * 计算积分
     *
     * @param actionType 事件类型
     * @param extParams  扩展参数
     * @return 应返回的积分值
     */
    Long calculate(String actionType, String extParams);

}
