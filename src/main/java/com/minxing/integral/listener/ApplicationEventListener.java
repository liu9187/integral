package com.minxing.integral.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @Author: maojunjun
 * @Description:
 * @Date: Created in 10:07 2018/4/26
 */
public class ApplicationEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = Logger.getLogger(ApplicationEventListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent ev) {
        // 防止重复执行
        if (ev.getApplicationContext().getParent() == null) {
            logger.info("start init message listener");
            ev.getApplicationContext().getBean(InitListener.class).init();
        }
    }
}