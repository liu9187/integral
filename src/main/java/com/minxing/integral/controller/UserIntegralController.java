package com.minxing.integral.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.minxing.integral.common.bean.Integral;
import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.util.ErrorJson;
import com.minxing.integral.common.util.StringUtil;
import com.minxing.integral.service.UserIntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "/removeUserIntegralByUserId", method = {RequestMethod.PUT})
    @ResponseBody
    public String removeUserIntegralByUserId(@RequestParam Integer userId,@RequestParam Long integral ) {
        JSONObject result = new JSONObject();
        logger.info("Receive exchange register request with userId:" + userId + "  integral: " + integral);

        if (userId == null || integral == null) {
            ErrorJson errorJson=new ErrorJson("20004","参数问题");
            return  errorJson.toJson();
        } else {
            try {
                     UserInfos userIntegral=new UserInfos();
                         userIntegral.setIntegral(integral);
                         userIntegral.setUserId(userId);
                int out = userIntegralService.removeUserIntegralByUserId(userIntegral);
                if (out > 0) {
                    result.put("message", "兑换成功");
                } else {
                    ErrorJson errorJson = new ErrorJson("20005", "积分余额不足");

                    return errorJson.toJson();

                }

            } catch (Exception e) {

                logger.error("Error in removeUserIntegralByUserId relation", e);
                e.getMessage();
            }
            return result.toJSONString();
        }


    }
    /**
     * 增加积分
     *
     * @param userId
     * @param integralStr
     * @return result.toJSONString()
     */
    @RequestMapping(value = "/addIntegral", method = {RequestMethod.POST} )
    @ResponseBody
    public String addIntegral(@RequestParam Integer userId,@RequestParam String integralStr ) {
        JSONObject result = new JSONObject();
        logger.info("Receive exchange register request with userId:" + userId + "  integralStr: " +integralStr );

        if (userId == null || StringUtil.isNull(integralStr)) {
            ErrorJson errorJson=new ErrorJson("20004","参数问题");
            return  errorJson.toJson();
        } else {
            try {

                Map<String,Object> params=new HashMap<String,Object>();
                //根据事件的类型查出对应的积分数
                Long integral=userIntegralService.selectIntegral(integralStr);
                switch (integralStr){
                    case "OCU_ARTICLE_READ":
                         params.put("integralId",1);
                         break;
                    case  "OCU_ARTICLE_COMMENT" :
                        params.put("integralId",2);
                         break;
                    case "OCU_ARTICLE_FORWARD":
                         params.put("integralId",3);
                         break;
                     default:
                         ErrorJson errorJson=new ErrorJson("20004","参数问题");
                         return  errorJson.toJson();

                }
                params.put("integral",integral);
                params.put("userId",userId);
                int out = userIntegralService.addIntegralByUserId(params);
                if (out > 0) {
                    result.put("message", "增加积分成功");
                } else {
                    ErrorJson errorJson = new ErrorJson("20004", "参数问题");

                    return errorJson.toJson();

                }

            } catch (Exception e) {

                logger.error("Error in removeUserIntegralByUserId relation", e);
                e.getMessage();
            }
            return result.toJSONString();
        }


    }

    /**
     * 设置积分规则 根据事件类型
     * @param type
     * @return
     */
    @RequestMapping(value = "/addIntegral", method = {RequestMethod.PUT} )
    @ResponseBody
    public String updateIntegralByType(@RequestParam String type){
             JSONObject jsonObject=new JSONObject();
          if (StringUtil.isNull(type)){
              ErrorJson errorJson = new ErrorJson("20004", "参数问题");
              return errorJson.toJson();
          }else{
           Integer out= userIntegralService.updateIntegralByType(type);
              if (out>0){
                   jsonObject.put("message","修改成功");
              }else {
                  ErrorJson errorJson = new ErrorJson("20003", "设置积分规则失败");
                  return errorJson.toJson();
              }
          }
        return jsonObject.toJSONString();
    }

    /**
     * 积分管理页面显示
     *
     * @param pageNum
     * @param pageSize
     * @param order
     */
    @RequestMapping(value = "/queryList", method = {RequestMethod.GET})
    @ResponseBody
    public String queryList( @RequestParam( defaultValue = "1",name ="pageNum") Integer pageNum,@RequestParam(defaultValue = "10",name ="pageSize") Integer pageSize,@RequestParam(defaultValue = "ASC",name="order") String order) {
        if (StringUtil.isNull(order)) {
            ErrorJson errorJson=new ErrorJson("20004","参数问题");
            return  errorJson.toJson();
        }
        PageHelper.startPage(pageNum, pageSize);
        List<IntegralManagementVO> vos = userIntegralService.queryList(order);
        PageInfo<IntegralManagementVO> pageInfo = new PageInfo<>(vos);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vos", vos);
        jsonObject.put("pages", pageInfo.getPages());//总页数
        jsonObject.put("total", pageInfo.getTotal());//总记录数
        jsonObject.put("code","200");
       return  jsonObject.toJSONString();


    }

    /**
     * 积分设置
     * @param
     * @param response
     */
    @RequestMapping(value = "/updateIntegral", method = {RequestMethod.PUT})
    @ResponseBody
    public String updateIntegral(@RequestParam Integer integralModification,HttpServletResponse response ){


                       if (integralModification.equals(null)){
                           ErrorJson errorJson=new ErrorJson("20004","参数问题");

                           return  errorJson.toJson();
                       }

                    JSONObject jsonObject=new JSONObject();

              try {

                  Integer result= userIntegralService.updateIntegral(integralModification);
                  jsonObject.put("result",result);
                  jsonObject.put("message","积分设置成功");
                  jsonObject.put("code","200");

              }catch (Exception e){
                       e.getMessage();
                      System.out.print("-----积分设置方法调用错误-------");
              }
               return   jsonObject.toJSONString();

    }

}
