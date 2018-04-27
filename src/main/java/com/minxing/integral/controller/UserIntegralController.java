package com.minxing.integral.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.exception.IntegrationErrorException;
import com.minxing.integral.common.exception.ParameterErrorException;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.pojo.vo.OrdinaryUserVO;
import com.minxing.integral.common.pojo.vo.SpecialUserVO;
import com.minxing.integral.common.util.ErrorJson;
import com.minxing.integral.common.util.StringUtil;
import com.minxing.integral.service.UserIntegralService;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户积分controller
 *
 * @author liucl
 * @date 2018-04-17
 */
@Controller
@RequestMapping("/api/v2/integral")
public class UserIntegralController {
    Logger logger = LoggerFactory.getLogger(UserIntegralController.class);

    @Autowired
    private UserIntegralService userIntegralService;

    /**
     * 积分兑换
     *
     * @param userId   用户id
     * @param integral 积分
     * @return result 积分兑换结果
     */
    @RequestMapping(value = "/removeUserIntegralByUserId", method = {RequestMethod.PUT})
    @ResponseBody
    public String removeUserIntegralByUserId(@RequestParam Integer userId, @RequestParam Long integral, HttpServletResponse response) throws Exception {
        JSONObject result = new JSONObject();
        // 接收到积分兑换请求
        logger.info("Receive integral exchange request with userId:" + userId + "  integral: " + integral);
        if (userId == null || integral == null) {
            ErrorJson errorJson = null;
            // 参数错误返回http状态码400
                throw new ParameterErrorException();
        } else {
            try {
                //封装到对象
                UserInfos userIntegral = new UserInfos();
                userIntegral.setIntegral(integral);
                userIntegral.setUserId(userId);
                // 尝试进行积分兑换
                int out = userIntegralService.removeUserIntegralByUserId(userIntegral);
                if (out > 0) {
                    result.put("message", "兑换成功");
                } else {
                    ErrorJson errorJson = null;
                    // 参数错误返回http状态码400
                        throw new IntegrationErrorException();
                }
            } catch (Exception e) {
                    throw new IntegrationErrorException();

            }
            return result.toJSONString();
        }
    }

    /**
     * 增加积分
     *
     * @param userId     用户id
     * @param extParams  扩展参数（为后续准备）
     * @param actionType 事件常量
     * @return result.toJSONString()
     */
    @RequestMapping(value = "/addIntegral", method = {RequestMethod.POST})
    @ResponseBody
    public Object addIntegral(@RequestParam String userId, @RequestParam String extParams, @RequestParam String actionType, HttpServletResponse response) throws Exception {
        logger.info("Receive exchange register request with userId:" + userId + " actionType:" + actionType);
        if (userId == null || StringUtil.isNull(actionType)) {
            ErrorJson errorJson = null;
                throw new ParameterErrorException();
        }
        Boolean res = userIntegralService.addIntegralByUserId(userId, actionType, extParams);
        if (!res) {
                throw new IntegrationErrorException();
            }
        response.setStatus(200);
        return "successful";
    }

    /**
     * 设置积分规则 根据事件类型
     *
     * @param type 积分增加类型
     * @return
     */
    @RequestMapping(value = "/updateIntegralByType", method = {RequestMethod.PUT})
    @ResponseBody
    public String updateIntegralByType(@RequestParam String type, @RequestParam Integer integral, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if (StringUtil.isNull(type) || integral == null) {
            ErrorJson errorJson = null;
                throw new ParameterErrorException();
        } else {
            Integer out = userIntegralService.updateIntegralByType(type, integral);
            if (out > 0) {
                jsonObject.put("message", "修改成功");
            } else {
                    throw new IntegrationErrorException();
            }
        }
        response.setStatus(200);
        return jsonObject.toJSONString();
    }

    /**
     * 积分管理页面显示
     *
     * @param pageNum  当前页
     * @param pageSize 当前页记录
     * @param order    排序
     */
    @RequestMapping(value = "/queryList", method = {RequestMethod.GET})
    @ResponseBody
    public String queryList(@RequestParam(defaultValue = "1", name = "pageNum") Integer pageNum, @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize,
                            @RequestParam(defaultValue = "1", name = "order") Integer order, @RequestParam(defaultValue = "integral") String type, HttpServletResponse response) {
        if (null == order) {
            ErrorJson errorJson = new ErrorJson("20004", "参数问题");
            return errorJson.toJson();
        }
        PageHelper.startPage(pageNum, pageSize);
        List<IntegralManagementVO> vos = userIntegralService.queryList(order);
        PageInfo<IntegralManagementVO> pageInfo = new PageInfo<>(vos);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vos", vos);
        //总页数
        jsonObject.put("pages", pageInfo.getPages());
        //总记录数
        jsonObject.put("total", pageInfo.getTotal());
        jsonObject.put("code", "200");
        response.setStatus(200);
        return jsonObject.toJSONString();


    }

    /**
     * 积分设置
     *
     * @param integralModification 积分设置参数
     * @return jsonObject  返回结果
     */
    @RequestMapping(value = "/updateIntegral", method = {RequestMethod.PUT})
    @ResponseBody
    public String updateIntegral(@RequestParam Integer integralModification, HttpServletResponse response) throws Exception{
        if (integralModification.equals(null)) {
            throw new IntegrationErrorException();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            Integer result = userIntegralService.updateIntegral(integralModification);
            jsonObject.put("result", result);
            jsonObject.put("message", "积分设置成功");
            jsonObject.put("code", "200");
        } catch (Exception e) {
                //未知异常
                throw new ParameterErrorException();
        }
        response.setStatus(200);
        return jsonObject.toJSONString();
    }

    /**
     * 初始化设置
     *
     * @return jsonObject.toJSONString() 设置的积分参数
     */
    @RequestMapping(value = "/selectExchange", method = {RequestMethod.GET})
    @ResponseBody
    public String selectExchange(HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            Integer exchange = userIntegralService.selectExchange();
            jsonObject.put("exchange", exchange);
            jsonObject.put("message", "初始化设置成功");
            jsonObject.put("code", "200");
        } catch (Exception e) {
                throw new ParameterErrorException();
        }
        response.setStatus(200);
        return jsonObject.toJSONString();
    }

    /**
     * 普通用户统计
     *
     * @param type      type 类型  阅读 read  评论 comment 合计 count
     * @param order     order  排序  0 降序  1升序
     * @param timeStart timeStart 开始时间
     * @param timeEnd   timeEnd 结束时间
     * @param pageNum   当前页
     * @param pageSize  当页记录数量
     * @return jsonObject.toJSONString()  数据列表 和分页数据
     */
    @RequestMapping(value = "/ordinaryUser", method = {RequestMethod.GET})
    @ResponseBody
    public String ordinaryUser(@RequestParam(defaultValue = "count") String type, @RequestParam(defaultValue = "1") Integer order
            , @RequestParam(required = false) Long timeStart, @RequestParam(required = false) Long timeEnd, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,HttpServletResponse response) throws Exception {
        logger.info("ordinaryUser params is type:" + type + " order:" + order + " timeStart:" + timeStart + " timeEnd:" + timeEnd + "pageNum:" + pageNum + "pageSize:" + pageSize);
        JSONObject jsonObject = new JSONObject();
        try {
            List<OrdinaryUserVO> vos = userIntegralService.ordinaryUser(type, order, timeStart, timeEnd, pageNum, pageSize);
            PageInfo<OrdinaryUserVO> page = new PageInfo(vos);
            //把分页信息和查询结果加入到json对象当中
            jsonObject.put("code", 200);
            jsonObject.put("pages", page.getPages());
            jsonObject.put("total", page.getTotal());
            jsonObject.put("vos", vos);
        } catch (Exception e) {
            //调用service 方法的时候出现错误
            logger.error("error is controller ordinaryUser:" + e);
        }
        response.setStatus(200);
        return jsonObject.toJSONString();
    }

    /**
     * 特殊用户统计
     *
     * @param type      type 类型  阅读 read  评论 comment  评论+转发 count1   总合计 count2
     * @param order     order  排序  0 降序  1升序
     * @param timeStart timeStart 开始时间
     * @param timeEnd   timeEnd 结束时间
     * @param pageNum   当前页
     * @param pageSize  当页记录数量
     * @return jsonObject.toJSONString()  数据列表 和分页数据
     */
    @RequestMapping(value = "/specialUser", method = {RequestMethod.GET})
    @ResponseBody
    public String specialUser(@RequestParam(defaultValue = "count2") String type, @RequestParam(defaultValue = "1") Integer order
            , @RequestParam(required = false) Long timeStart, @RequestParam(required = false) Long timeEnd, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,HttpServletResponse response)throws Exception {
        logger.info("ordinaryUser params is type:" + type + " order:" + order + " timeStart:" + timeStart + " timeEnd:" + timeEnd + "pageNum:" + pageNum + "pageSize:" + pageSize);
        JSONObject jsonObject = new JSONObject();
        try {
            List<SpecialUserVO> vos = userIntegralService.specialUser(type, order, timeStart, timeEnd, pageNum, pageSize);
            PageInfo<OrdinaryUserVO> page = new PageInfo(vos);
            //把数据封装到json对象当中
            jsonObject.put("code", 200);
            jsonObject.put("pages", page.getPages());
            jsonObject.put("total", page.getTotal());
            jsonObject.put("vos", vos);
        } catch (Exception e) {
            //调用业务的时候出现错误
            logger.error("error controller specialUser:" + e);
        }
        response.setStatus(200);
        return jsonObject.toJSONString();
    }

}
