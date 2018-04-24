package com.minxing.integral.service;

import com.alibaba.fastjson.JSON;
import com.minxing.token.CookieSession;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URLDecoder;
import java.util.Date;

public class UserService {

//    static Logger logger = LoggerFactory.getLogger(UserService.class);
//    private static UserDao userDao;
//
//    static {
//        userDao = new UserDao();
//    }
//
//    public static Long findAccountIdBySession(Request request) {
//        String sessionId = request.headers("_session_id");
//        //        logger.info("cookie: " + sessionId);
//        if (sessionId == null) {
//            logger.error("sessionId is null");
//            return null;
//        }
//        return CookieSession.unmarshalAccountIdFromCookie(URLDecoder.decode(sessionId.split("--")[0]));
//    }
//
//    public static User findUser(Request request, boolean isCache) {
//        String authorization = request.headers("AUTHORIZATION");
//        logger.info("AUTHORIZATION: " + authorization);
//        String networkId = request.headers("NETWORK-ID");
//        logger.info("NETWORK-ID: " + networkId);
//        if (StringUtils.isNotEmpty(networkId) && StringUtils.isNotEmpty(authorization)) {
//            String token = authorization.trim().substring(7).trim();
//            logger.info("token: " + token);
//            User user = userDao.findUser(token, Integer.valueOf(networkId), isCache);
//            if (user != null) {
//                user.setBearerToken(token);
//                return user;
//            }
//        }
//        return null;
//    }
//
//    public static User findUserRequest_(Request request, boolean isCache) {
//        User user = findUser(request, isCache);
//        /*if (user != null) {
//            user.setOpenId(ocuId);
//        }*/
//        logger.info("user: " + JSON.toJSONString(user));
//        return user;
//    }
//
//    public static User findUserRequest(Request request, boolean isCache) {
//        User user = findUser(request, isCache);
//        String domainId = request.headers("DOMAIN-ID");
//        logger.info("DOMAIN-ID: " + domainId);
//        if (user != null) {
//            /*if (Assert.notBlank_(domainId)) {
//				user.setDomainId(domainId);
//			}*/
//            if (Assert.blank_(domainId)) {
//                logger.error("header里DOMAIN-ID为空");
//                boolean isXietong = MxApiConfig.getInstance().isXietong();
//                if (isXietong) {
//                    domainId = String.valueOf(userDao.findDomainIdByDeptId(user.getDepartmentId()));
//                } else {
//                    domainId = String.valueOf(userDao.findDomainIdByUserId(user.getUserId()));
//                }
//                /*if (Assert.notBlank_(domainId)) {
//                    user.setDomainId(domainId);
//                }*/
//            }
//            if (Assert.notBlank_(domainId)) {
//                user.setOpenId(new OcusDao().findOpenIdByDomainId(Integer.parseInt(domainId)));
//            }
//
//        }
//        logger.info("user: " + JSON.toJSONString(user));
//        return user;
//    }
//
//    public static void main(String[] args){
////        User user = userDao.findUser("BKwSroSgBOFlUFFELf1GoUMCcmDWxQOvOSkO-GV2AKemwta3", 2, false);
////        System.out.println(user.toString());
//    }
//
//    public static Integer findUserIdByRequestHeader(Request request) {
//        try {
//            String authorization = request.headers("AUTHORIZATION");
//            String networdId = request.headers("NETWORK-ID");
//            logger.info("authorization: " + authorization + " networdId: " + networdId);
////            logger.info("authorization.substring(7) " + authorization.substring(7));
//            if (StringUtils.isEmpty(networdId)) {
//                networdId = request.queryParams("nd");
//            }
//            logger.info("networdId: " + networdId);
//            UserMapper mapper = SessionFactoryBuilder.getSqlSession().getMapper(UserMapper.class);
//            if (StringUtils.isNotEmpty(networdId)) {
//                logger.info("StringUtils.isNotEmpty(" + networdId + ")");
//                Oauth2AccessToken oauth2AccessToken = null;
//                Long accountId = null;
//                if (StringUtils.isEmpty(authorization)) {
//                    logger.info("StringUtils.isEmpty(authorization) is ture");
//                    //尝试从session中获取accountId
//                    String sessionId = request.cookie("_session_id");
//                    if (StringUtils.isNotEmpty(sessionId))
//                        accountId =
//                                CookieSession.unmarshalAccountIdFromCookie(URLDecoder.decode(sessionId.split("--")[0]));
//                } else {
//                    oauth2AccessToken = mapper.findAccountByToken(authorization.substring(7).trim());
//                    if (oauth2AccessToken.getExpiredTime().before(new Date())) {
//                        logger.info("StringUtils.isEmpty(authorization) is false");
//                        //尝试从session中获取accountId
//                        logger.error("The token is expired.");
//                    } else {
//                        logger.info("Oauth2AccessToken: " + JSON.toJSONString(oauth2AccessToken));
//                        accountId = oauth2AccessToken.getAccountId();
//                    }
//                }
//                logger.info("accountId: " + accountId);
//                Integer uid =
//                        mapper.findUidByAccountIdAndNetWorkId(accountId, Integer.valueOf(networdId));
//                return uid;
//            } else {
//                logger.error("The network-id is empty!");
//            }
//        } catch (Exception e) {
//            logger.error("findUserIdByRequestHeader", e);
////            return null;
//        }
////        logger.info("不应该走着这");
 //       return null;
 //   }
}
