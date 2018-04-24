package com.minxing.integral.service.impl;

import com.minxing.integral.common.bean.UserIntegral;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.dao.UserIntegralMapper;
import com.minxing.integral.service.UserIntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserIntegralServiceImpl implements UserIntegralService {

    Logger logger = LoggerFactory.getLogger(UserIntegralServiceImpl.class);

    @Autowired
    private UserIntegralMapper userIntegralMapper;

    /**
     * 积分兑换
     *
     * @param userIntegral
     * @return
     */
    @Override
    @Transactional
    public Integer removeUserIntegralByUserId(UserIntegral userIntegral) {
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
     * @param userIntegral
     * @return
     */
    @Override
    public Integer addIntegralByUserId(UserIntegral userIntegral) {
        return userIntegralMapper.addIntegralByUserId(userIntegral);
    }


}