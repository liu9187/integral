package com.minxing.integral.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.minxing.integral.common.bean.Integral;
import com.minxing.integral.common.bean.IntegralRecord;
import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.util.ErrorJson;
import com.minxing.integral.dao.UserIntegralMapper;
import com.minxing.integral.service.UserIntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserIntegralServiceImpl implements UserIntegralService {

    Logger logger = LoggerFactory.getLogger(UserIntegralServiceImpl.class);

    @Autowired
    private UserIntegralMapper userIntegralMapper;

    @Value("${event_type_is_valid}")
    private String isValidEvent;

    /**
     * 积分兑换
     *
     * @param userIntegral
     * @return
     */
    @Override
    @Transactional
    public Integer removeUserIntegralByUserId(UserInfos userIntegral) {
       Long integral= userIntegralMapper.queryIntegralByUserId(userIntegral.getUserId());
         if (integral<userIntegral.getIntegral()){
             return -1;
         }
        return userIntegralMapper.removeUserIntegralByUserId(userIntegral);
    }

    /**
     * 积分系统显示
     *
     * @param order
     * @return
     */
    @Override
    public List<IntegralManagementVO> queryList(String order) {
        List<IntegralManagementVO> list=new ArrayList<>();
        try {

            if (order.equals("ASC")) {
                list= userIntegralMapper.queryListByASC();
            } else if (order.equals("DESC")) {
                list= userIntegralMapper.queryListByDESC();
            } else {
                System.out.print("积分显示方法输入参数出现错误" + order);

            }
        } catch (Exception e) {
            logger.error("The error is queryList" + e);
        }

        return list;

    }

    /**
     * 积分设置
     * @param integralModification
     * @return
     */
    @Override
    @Transactional
    public Integer updateIntegral(Integer integralModification) {
        return userIntegralMapper.updateIntegral(integralModification);
    }

    /**
     * 增加积分
     * @param
     * @return
     */
    @Override
    @Transactional
    public Boolean addIntegralByUserId(String userId, String actionType, String extParams) {
        try {
            //判断是否是有效事件(只有阅读)
            if(isValidEvent.equals(actionType)){
                JSONObject json = JSONObject.parseObject(extParams);
                Object articleId = json.get("article_id");
                Integer res = userIntegralMapper.findIsValid(Integer.valueOf(userId), Integer.valueOf(articleId.toString()));
                if(res > 0){
                    logger.info("userId" +userId+ "and articleId" +articleId+ "is not valid event");
                    return true;
                }
                //新增记录 下次阅读为无效事件
                Integer ins = userIntegralMapper.addValidEvent(Integer.valueOf(userId), Integer.valueOf(articleId.toString()), new Date());
                if(1 != ins){
                    logger.error("add valid event error");
                    return false;
                }
            }
            //根据事件的类型查出对应积分数据
            Integral integral = userIntegralMapper.selectIntegral(actionType);
            if(null == integral){
                logger.error("integral number error");
                return false;
            }
            //增加积分
            Integer res = userIntegralMapper.addIntegralByUserId(Integer.valueOf(userId), integral.getIntegral().intValue());
            if(1 != res){
                logger.error("add integral error");
                return false;
            }
            //记录此次事件
            IntegralRecord integralRecord = new IntegralRecord();
            integralRecord.setIntegralId(integral.getId());
            integralRecord.setUserId(userId);
            integralRecord.setCreateDate(new Date().getTime()/1000);

            Integer rest = userIntegralMapper.insertIntegralRecord(integralRecord);
            if(1 != rest){
                logger.error("add integral_record error");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("event operation error");
            return false;
        }
        return true;
    }

    /**
     * 修改积分规则
     * 每次事件对应积分数
     * @param type
     * @return
     */
    @Override
    public Integer updateIntegralByType(String type) {
        return userIntegralMapper.updateIntegralByType(type);
    }


}
