package com.minxing.integral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by SuZZ on 2018/1/4.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.minxing.integral")
@EnableTransactionManagement
@ServletComponentScan(basePackages = "com.minxing.integral.filter")
public class StartUp {

    public static void main(String[] args){
        SpringApplication.run(StartUp.class,args);
    }

}
