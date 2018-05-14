package com.minxing.integral.filter;
import com.minxing.integral.common.util.ErrorJson;
import com.minxing.integral.common.util.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;


/**
 * license校验
 * @author liuchanglong
 * @date 2018-5-14
 */
public class LicenseFilter implements Filter {
    static Logger logger  = LoggerFactory.getLogger(LicenseFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //license校验
             License license=new License();
        HttpServletRequest  req= (HttpServletRequest) request;
        HttpServletResponse res= (HttpServletResponse) response;
        if (!license.checkLicense()) {
            ErrorJson errorJson = new ErrorJson("20002", "产品License不支持此功能或此功能已过期，请联系管理员。");
            logger.error("doFilter checkLicense error[code:400; requestUri:" + req.getRequestURI() + "]>>>产品License不支持此功能或此功能已过期，请联系管理员。");
            res.setStatus(400);
            res.setHeader("Content-Type", "application/json");
            ServletOutputStream out = response.getOutputStream();
            out.write(errorJson.toJson().getBytes(Charset.forName("utf-8")));
            out.flush();
            out.close();
            return;
        }
    }

    @Override
    public void destroy() {

    }
}
