package com.minxing.integral.service;

import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;

import java.util.List;
import java.util.Map;

public interface UserIntegralService {
    /**
     * 积分兑换服务
     * @param userIntegral
     * @return
     */
    Integer removeUserIntegralByUserId(UserInfos userIntegral);

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
     * @param params
     * @return
     */
    Integer addIntegralByUserId(Map params);
    /**
     * 根据事件查询对应时间的积分
     * @param type
     * @return integral
     */
    Long selectIntegral(String type);

    /**
     * 修改积分规则
     * 每次事件对应积分数
     * @param type
     * @return
     */
    Integer updateIntegralByType(String type);

}
