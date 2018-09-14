package com.minxing.integral.handler;

import com.alibaba.fastjson.JSONObject;
import com.minxing.integral.common.exception.*;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @author SuZZ on 2018/3/8.
 */
@ControllerAdvice
public class ExchangeExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ExchangeExceptionHandler.class);
    private static final Integer DATE_ERROR =20014;
    private static final Integer POINTS_EXCHANGE_ERROR =20013;
    private static final Integer INTEGRAL_ERROR =20012;
    private static final Integer PARAMETER_ERROR = 20004;
    private static final Integer UNKNOWN_ERROR = 22222;
    private static final Integer TOKEN_BAD = 20000;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JSONObject handler(HttpServletResponse response, Exception e) {
        if (e instanceof ParameterErrorException) {
            // 参数错误,返回错误数据
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            // 返回错误的数据
            return generateErrorMsg("参数问题", PARAMETER_ERROR);
        }else if (e instanceof IntegrationErrorException) {
            // 积分设置错误,返回错误数据
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            // 返回错误的数据
            return generateErrorMsg("设置失败，请重试", INTEGRAL_ERROR);
        }else if (e instanceof TokenBadException || e instanceof TokenDecryptException) {
            // 用户信息校验失败
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            // 错误数据返回
            return generateErrorMsg("获取权限失败", TOKEN_BAD);
        }else if (e instanceof PointsExchangeException){
            // 积分兑换错误,返回错误数据
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            // 返回错误的数据
            return generateErrorMsg("积分余额不足无法兑换", POINTS_EXCHANGE_ERROR);
        }else if (e instanceof DateErrorException){
            // 积分兑换错误,返回错误数据
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            // 返回错误的数据
            return generateErrorMsg("结束时间大于开始时间", DATE_ERROR);
        }
        logger.error("Unknown abnormal",e);
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return generateErrorMsg("服务器异常", UNKNOWN_ERROR);
    }

    /**
     * 生成错误的信息用以返回
     *
     * @param message 信息
     * @param code    状态码
     * @return 返回json
     */
    private JSONObject generateErrorMsg(String message, Integer code) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        jsonObject.put("status_code", code);
        JSONObject result = new JSONObject();
        result.put("errors", jsonObject);
        return result;
    }


}
