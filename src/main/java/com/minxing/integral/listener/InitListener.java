package com.minxing.integral.listener;


import com.minxing.integral.dao.InitMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: maojunjun
 * @Description:
 * @Date: Created in 10:16 2018/4/26
 */
@Component
public class InitListener {

    Logger logger = LoggerFactory.getLogger(InitListener.class);

    @Autowired
    private InitMapper initMapper;

    @Value("${read.default.integral}")
    private Integer readIntegral;

    @Value("${comment.default.integral}")
    private Integer commentIntegral;

    @Value("${forward.default.integral}")
    private Integer forwardIntegral;

    /**
     * 初始化数据库资源
     */
    public void init(){
        logger.info("start init integral");

        //user_info 尝试增加integral字段        ruby创建此字段
//        try {
//            initMapper.findIntegralFromUserInfo();
//        }catch (Exception e){
//            logger.info("the integral not exist from user_info, start add the column integral");
//            initMapper.addColumnIntegral();
//        }

        //尝试创建integral表
        try {
            initMapper.findIntegral();
        }catch (Exception e){
            logger.info("Table integral not exist, Start to create table");
            initMapper.initIntegral();
            //设置初始值
            initMapper.initTableIntegralInfos(readIntegral, commentIntegral, forwardIntegral);
        }

        //尝试创建integral_record表（事件记录表）
        try {
            initMapper.findIntegralRecord();
        }catch (Exception e){
            logger.info("Table integral_record not exist, Start to create table");
            initMapper.initIntegralRecord();
        }

        //尝试创建integral_exchange表
        try {
            initMapper.findIntegralExchange();
        }catch (Exception e){
            logger.info("Table integral_exchange not exist, Start to create table");
            initMapper.initIntegralExchange();
        }

        //尝试创建valid_event表（有效事件表）
        try {
            initMapper.findValidEvent();
        }catch (Exception e){
            logger.info("Table valid_event not exist, Start to create table");
            initMapper.initValidEvent();
        }
    }
}
