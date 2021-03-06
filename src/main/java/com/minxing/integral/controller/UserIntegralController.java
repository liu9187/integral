package com.minxing.integral.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.exception.DateErrorException;
import com.minxing.integral.common.exception.IntegrationErrorException;
import com.minxing.integral.common.exception.ParameterErrorException;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.pojo.vo.OrdinaryUserVO;
import com.minxing.integral.common.pojo.vo.SpecialUserVO;
import com.minxing.integral.common.util.ErrorJson;
import com.minxing.integral.common.util.StringUtil;
import com.minxing.integral.service.UserIntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    Logger logger = LoggerFactory.getLogger( UserIntegralController.class );

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
    public String removeUserIntegralByUserId(@RequestParam(name = "userId", required = false) Integer userId, @RequestParam(name = "integral", required = false) Long integral, HttpServletResponse response) throws Exception {
        // 接收到积分兑换请求
        String result = new String();
        logger.info( "Receive integral exchange request with userId:" + userId + "  integral: " + integral );
        if (userId == null || integral == null) {
            // 参数错误返回http状态码400
            throw new ParameterErrorException();
        } else {
            try {
                //封装到对象
                UserInfos userIntegral = new UserInfos();
                userIntegral.setIntegral( integral );
                userIntegral.setUserId( userId );
                // 尝试进行积分兑换
                 result = userIntegralService.removeUserIntegralByUserId( userIntegral );

            } catch (Exception e) {
                logger.error( "error controller  removeUserIntegralByUserId" + e );
                throw e;
            }
            return result;
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
    public String addIntegral(@RequestParam String userId, @RequestParam(name = "extParams",defaultValue = "") String extParams, @RequestParam String actionType, HttpServletResponse response) throws Exception {
        logger.info( "Receive exchange register request with userId:" + userId + " actionType:" + actionType );
        if (userId == null || StringUtil.isNull( actionType )) {
            throw new ParameterErrorException();
        }
        Boolean res = userIntegralService.addIntegralByUserId( userId, actionType, extParams );
        if (!res) {
            throw new IntegrationErrorException();
        }
        response.setStatus( 200 );
        JSONObject jsonObject=new JSONObject(  );
           jsonObject.put( "message" ,"ok");
        return jsonObject.toJSONString();
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
        if (StringUtil.isNull( type ) || integral == null) {
            throw new ParameterErrorException();
        } else {
            Integer out = userIntegralService.updateIntegralByType( type, integral );
            if (out > 0) {
                jsonObject.put( "message", "修改成功" );
            } else {
                throw new IntegrationErrorException();
            }
        }
        response.setStatus( 200 );
        return jsonObject.toJSONString();
    }

    /**
     * 积分勋值统计/兑换管理
     *
     * @param pageNum  当前页
     * @param pageSize 当前页记录
     * @param order    排序
     */
    @RequestMapping(value = "/queryList", method = {RequestMethod.GET})
    @ResponseBody
    public String queryList(@RequestParam(defaultValue = "1", name = "pageNum") Integer pageNum, @RequestParam(defaultValue = "20", name = "pageSize") Integer pageSize, @RequestParam(name = "nameStr", required = false) String nameStr, @RequestParam(defaultValue = "0", name = "order") Integer order, @RequestParam(defaultValue = "integral" ,name = "type") String type, HttpServletResponse response, HttpServletRequest request) {
        if (null == order) {
            ErrorJson errorJson = new ErrorJson( "20004", "参数问题" );
            return errorJson.toJson();
        }
        String networkId = null;
        try {
             //networkId=String.valueOf( 3 );
             networkId = (String) request.getSession().getAttribute( "networkId" );
        } catch (Exception e) {
            logger.error( "error is controller querList networkId" );
            new ParameterErrorException();
        }
        //分页插件
        PageHelper.startPage( pageNum, pageSize );
        List<IntegralManagementVO> vos = userIntegralService.queryList( networkId, nameStr, order ,type);
        PageInfo<IntegralManagementVO> pageInfo = new PageInfo<>( vos );
        JSONObject jsonObject = new JSONObject();
        jsonObject.put( "vos", vos );
        //总页数
        jsonObject.put( "pages", pageInfo.getPages() );
        //总记录数
        jsonObject.put( "total", pageInfo.getTotal() );
        jsonObject.put( "code", "200" );
        response.setStatus( 200 );
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
    public String updateIntegral(@RequestParam Long integralModification, HttpServletResponse response) throws Exception {
        if (null == integralModification) {
            throw new IntegrationErrorException();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            Integer result = userIntegralService.updateIntegral( integralModification );
            jsonObject.put( "result", result );
            jsonObject.put( "message", "积分设置成功" );
            jsonObject.put( "code", "200" );
        } catch (Exception e) {
            //未知异常
            logger.error( "error controller updateIntegral" + e );
            throw e;
        }
        response.setStatus( 200 );
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
            jsonObject.put( "exchange", exchange );
            jsonObject.put( "message", "初始化设置成功" );
            jsonObject.put( "code", "200" );
        } catch (Exception e) {
            throw new ParameterErrorException();
        }
        response.setStatus( 200 );
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
    public String ordinaryUser(@RequestParam(defaultValue = "count") String type, @RequestParam(defaultValue = "0") Integer order, @RequestParam(required = false) Long timeStart, @RequestParam(name = "nameStr", required = false) String nameStr, @RequestParam(required = false) Long timeEnd, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "20") Integer pageSize, HttpServletResponse response, HttpServletRequest request) throws Exception {
        logger.info( "ordinaryUser params is type:" + type + " order:" + order + " timeStart:" + timeStart + " timeEnd:" + timeEnd + "pageNum:" + pageNum + "pageSize:" + pageSize );
        if (null != timeStart && null != timeEnd) {
            if (timeStart < timeEnd) {
                //开始时间大于结束时间
                logger.error( "error is timeStart and timeEnd  " );
                new DateErrorException();
            }
        }

        JSONObject jsonObject = new JSONObject();
        String networkId = "3";
        try {
            //获取 networkId
            networkId = (String) request.getSession().getAttribute( "networkId" );
        } catch (Exception e) {
            logger.error( "error is controller querList networkId" );
            new ParameterErrorException();
        }
        try {
            List<OrdinaryUserVO> vos = userIntegralService.ordinaryUser( type, order, timeStart, timeEnd, pageNum, pageSize, networkId, nameStr );
            PageInfo<OrdinaryUserVO> page = new PageInfo( vos );
            //把分页信息和查询结果加入到json对象当中
            jsonObject.put( "code", 200 );
            jsonObject.put( "pages", page.getPages() );
            jsonObject.put( "total", page.getTotal() );
            jsonObject.put( "vos", vos );
        } catch (Exception e) {
            //调用service 方法的时候出现错误
            logger.error( "error is controller ordinaryUser:" + e );
            throw e;
        }
        response.setStatus( 200 );
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
    public String specialUser(@RequestParam(defaultValue = "count2") String type, @RequestParam(defaultValue = "0") Integer order, @RequestParam(name = "nameStr", required = false) String nameStr, @RequestParam(required = false) Long timeStart, @RequestParam(required = false) Long timeEnd, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "20") Integer pageSize, HttpServletResponse response, HttpServletRequest request) throws Exception {
        logger.info( "ordinaryUser params is type:" + type + " order:" + order + " timeStart:" + timeStart + " timeEnd:" + timeEnd + "pageNum:" + pageNum + "pageSize:" + pageSize );
        if (null != timeStart && null != timeEnd) {
            if (timeStart < timeEnd) {
                //开始时间大于结束时间
                logger.error( "error is timeStart and timeEnd  " );
                new DateErrorException();
            }
        }
        JSONObject jsonObject = new JSONObject();
        String networkId = null;
        try {
            //获取 networkId
            networkId = (String) request.getSession().getAttribute( "networkId" );
        } catch (Exception e) {
            logger.error( "error is controller querList networkId" );
            new ParameterErrorException();
        }
        try {
            List<SpecialUserVO> vos = userIntegralService.specialUser( type, order, timeStart, timeEnd, pageNum, pageSize, networkId, nameStr );
            PageInfo<OrdinaryUserVO> page = new PageInfo( vos );
            //把数据封装到json对象当中
            jsonObject.put( "code", 200 );
            jsonObject.put( "pages", page.getPages() );
            jsonObject.put( "total", page.getTotal() );
            jsonObject.put( "vos", vos );
        } catch (Exception e) {
            //调用业务的时候出现错误
            logger.error( "error controller specialUser:" + e );
        }
        response.setStatus( 200 );
        return jsonObject.toJSONString();
    }

    /**
     * 勋值查询 显示
     *
     * @param userId
     * @return 勋值 积分
     */
    @RequestMapping(value = "/selectMeritByUserId", method = {RequestMethod.GET})
    @ResponseBody
    public String selectMeritByUserId(@RequestParam(name = "userId") Integer userId) {
        JSONObject jb = new JSONObject();
        if (null != userId) {
            UserInfos userInfos = userIntegralService.selectMeritByUserId( userId );
            jb.put( "userInfos", userInfos );
        } else {
            logger.error( "<<<<<<<<<<传入参数错误：userId=" + userId );
            ErrorJson errorJson = new ErrorJson( "20004", "<<<<selectMeritByUserId参数错误" );
            return errorJson.toJson();
        }

        return jb.toJSONString();
    }

}
