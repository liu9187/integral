package com.minxing.integral.filter;

import com.alibaba.fastjson.JSON;
import com.minxing.integral.common.bean.Oauth2AccessToken;
import com.minxing.integral.common.util.ErrorJson;
import com.minxing.integral.dao.UserMapper;
import com.minxing.token.CookieSession;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

/**
 * @author SuZZ on 2018/4/24.
 */
@WebFilter(filterName = "Filter1_integral",urlPatterns = {"/api/v2/integral/removeUserIntegralByUserId","/api/v2/integral/updateIntegralByType",
        "/api/v2/queryList","/api/v2/integral/updateIntegral",
        "/api/v2/integral/selectExchange","/api/v2/integral/ordinaryUser","/api/v2/integral/specialUser","/api/v2/integral/selectMeritByUserId"})
public class IntegralFilter implements Filter {

    static Logger logger  = LoggerFactory.getLogger(IntegralFilter.class);

    @Autowired
    UserMapper userMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // 没有session,开始校验身份
        Integer uid = findUserIdByRequestHeader(request);
        if (uid != null){
            // TODO 校验权限
            filterChain.doFilter(request,response);
            return ;
        }
        // 还是没有,返回无权限
        ((HttpServletResponse) res).setStatus(401);
        response.getWriter().write(new ErrorJson("permission validation failed", "20000").toJson());
        response.getWriter().flush();
    }

    @Override
    public void destroy() {

    }

    public Integer findUserIdByRequestHeader(HttpServletRequest request) {
        try {
            String authorization = request.getHeader("AUTHORIZATION");
            String networkId = request.getHeader("NETWORK-ID");
            Cookie[] cookies = request.getCookies();
            if (StringUtils.isEmpty(networkId)) {
                if (cookies != null && cookies.length != 0) {
                    for (Cookie cookie : cookies) {
                        if ("mx_network_id".equals(cookie.getName())) {
                            networkId = cookie.getValue();
                            break;
                        }
                    }
                }
            }
            logger.info("authorization: " + authorization + " networkId: " + networkId);
//            // FIXME network id 取不到,临时解决方案
//            if (StringUtils.isEmpty(networkId)){
//                networkId = "3";
//            }
            //把 networkId 存到 对象
//            Network network=new Network();
//                  network.setNetworkId(networkId);
               request.getSession().setAttribute( "networkId" ,networkId);
            if (StringUtils.isNotEmpty(networkId)) {
                logger.info("network-id: " + networkId);
                Oauth2AccessToken oauth2AccessToken = null;
                Long accountId = null;
                if (StringUtils.isEmpty(authorization)) {
                    logger.info("StringUtils.isEmpty(authorization) is ture");
                    //尝试从session中获取accountId
                    String sessionId = null;
                    if (cookies != null && cookies.length != 0) {
                        for (Cookie cookie : cookies) {
                            if (cookie.getName().equals("_session_id")) {
                                sessionId = cookie.getValue();
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(sessionId)) {
                        accountId =
                                CookieSession.unmarshalAccountIdFromCookie(URLDecoder.decode(sessionId.split("--")[0]));
                    }
                } else {
                    oauth2AccessToken = userMapper.findAccountByToken(authorization.substring(7).trim());
                    if (oauth2AccessToken == null) {
                        return null;
                    }
                    if (oauth2AccessToken.getExpiredTime().before(new Date())) {
                        logger.info("StringUtils.isEmpty(authorization) is false");
                        //尝试从session中获取accountId
                        logger.error("The token is expired.");
                    } else {
                        logger.info("Oauth2AccessToken: " + JSON.toJSONString(oauth2AccessToken));
                        accountId = oauth2AccessToken.getAccountId();
                    }
                }
                logger.info("accountId: " + accountId);
                Integer uid = userMapper.checkNetWorkAdmin(networkId, accountId);
                return uid;
            } else {
                logger.error("The network-id is empty!");
            }
        } catch (Exception e) {
            logger.error("findUserIdByRequestHeader", e);
        }
        return null;
    }


}
