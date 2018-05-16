package com.minxing.integral.filter;

import com.minxing.integral.common.util.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author SuZZ on 2018/5/14.
 */
@WebFilter(filterName = "Filter0_license",urlPatterns = {"/api/v2/integral/*"})
public class LicenseFilter implements Filter {

    @Autowired
    private License license;
    @Value("${license.key}")
    private String licenseKey;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // FIXME 暂时关闭了license校验,方便调试使用
        Boolean licenseKey = license.checkLicenseKey("licenseKey");
//        Boolean licenseKey = true;
        if (licenseKey) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        } else {
            // 还是没有,返回无权限
            ((HttpServletResponse) servletResponse).setStatus(400);
            servletResponse.getWriter().write("{\"errors\":{\"message\":\"产品License不支持此功能或此功能已过期，请联系管理员。(2)\",\"status_code\":\"20000\"}}");
            servletResponse.getWriter().flush();
        }
    }

    @Override
    public void destroy() {

    }
}
