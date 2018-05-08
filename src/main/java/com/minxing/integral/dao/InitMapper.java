package com.minxing.integral.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: maojunjun
 * @Description:
 * @Date: Created in 10:26 2018/4/26
 */
public interface InitMapper {


    @Select("SELECT integral FROM user_infos LIMIT 0,1")
    Integer findIntegralFromUserInfo();

    @Select("ALTER TABLE user_infos ADD integral INTEGER(11) DEFAULT NULL;")
    void addColumnIntegral();

    @Select("SELECT id FROM integral LIMIT 0,1")
    Integer findIntegral();

    @Select("CREATE TABLE `integral` (\n" +
            "  `id` int(11) NOT NULL,\n" +
            "  `type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '阅读, 转发，评论 等',\n" +
            "  `integral` int(11) DEFAULT NULL COMMENT '相应的类型',\n" +
            "  PRIMARY KEY (`id`)\n" +
            ")")
    void initIntegral();

    @Insert("INSERT INTO `integral` (`id`, `type`, `integral`) VALUES " +
            "('1', 'OCU_ARTICLE_READ', #{readIntegral})," +
            "('2', 'OCU_ARTICLE_COMMENT', #{commentIntegral})," +
            "('3', 'OCU_ARTICLE_FORWARD', #{forwardIntegral});\n")
    void initTableIntegralInfos(@Param("readIntegral") Integer readIntegral, @Param("commentIntegral") Integer commentIntegral, @Param("forwardIntegral") Integer forwardIntegral);

    @Select("SELECT id FROM integral_record LIMIT 0,1")
    Integer findIntegralRecord();

    @Select("CREATE TABLE `integral_record` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `integral_id` int(11) DEFAULT NULL,\n" +
            "  `user_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,\n" +
            "  `create_date` int(13) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ")")
    void initIntegralRecord();

    @Select("SELECT id FROM integral_exchange LIMIT 0,1")
    Integer findIntegralExchange();

    @Select("CREATE TABLE `integral_exchange` (\n" +
            "  `id` int(11) NOT NULL,\n" +
            "  `integral_exchange` int(11) DEFAULT NULL,\n" +
            "  `create_date` datetime DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ")")
    void initIntegralExchange();

    @Select("SELECT id FROM valid_event LIMIT 0,1")
    Integer findValidEvent();

    @Select("CREATE TABLE `valid_event` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
            "  `user_id` int(11) NOT NULL COMMENT '用户id',\n" +
            "  `article_id` int(11) NOT NULL COMMENT '事件id',\n" +
            "  `create_date` datetime NOT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ")")
    void initValidEvent();

    @Insert("INSERT INTO integral_exchange(id,integral_exchange,create_date) VALUES (1,200,NOW())")
    void initAddIntegralExchange();
}
