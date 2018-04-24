package com.minxing.integral.service;

import com.minxing.integral.common.bean.UserIntegral;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserIntegralService {
    /**
     * 积分兑换服务
     * @param userIntegral
     * @return
     */
    Integer removeUserIntegralByUserId(UserIntegral userIntegral);

    /**
     * 积分管理数据显示
     * @return
     */
    List<IntegralManagementVO> queryList(String order);

    /**
     * 积分设置
     * @param integralModification
     * @return
     */
    Integer updateIntegral( Integer integralModification);

    /**
     * 增加积分
     * @param userIntegral
     * @return
     */
    Integer addIntegralByUserId(UserIntegral userIntegral);
}
