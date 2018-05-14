package com.minxing.integral.config;

import com.minxing.integral.filter.IntegralFilter;
import com.minxing.integral.filter.LicenseFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置
 * @author liuchanglong
 * @date 2018-05-14
 */
@Configuration
public class FilterConfig {
    /**
     * 配置 LicenseFilter 过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean licenseFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter( new LicenseFilter());
        registration.addUrlPatterns("/api/v2/integral/*");
        registration.setName("licenseFilter");
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }
    /**
     * 配置 integralFilter 过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean integralFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new IntegralFilter());
        registration.addUrlPatterns( "/api/v2/integral/removeUserIntegralByUserId","/api/v2/integral/updateIntegralByType","/api/v2/integral/queryList","/api/v2/integral/updateIntegral","/api/v2/integral/selectExchange","/api/v2/integral/ordinaryUser","/api/v2/integral/specialUser");
        registration.setName("integralFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

}
