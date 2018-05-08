package com.minxing.integral.service;

import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.pojo.vo.OrdinaryUserVO;
import com.minxing.integral.common.pojo.vo.SpecialUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户积分service
 * @author liucl
 * @date 2018-4-27
 */
public interface UserIntegralService {
    /**
     * 积分兑换服务
     * @param userIntegral
     * @return
     */
    Integer removeUserIntegralByUserId(UserInfos userIntegral);

    /**
     * 积分管理数据显示
     * @param order 排序 0 降序  1升序
     * @return
     */
    List<IntegralManagementVO> queryList(Integer order,String networkId);

    /**
     * 积分设置
     * @param integralModification
     * @return
     */
    Integer updateIntegral( Integer integralModification);

    /**
     * 增加积分
     * @param
     * @return
     */
    Boolean addIntegralByUserId(String userId, String actionType, String extParams);

    /**
     * 修改积分规则
     * 每次事件对应积分数
     * @param type
     * @return
     */
    Integer updateIntegralByType(String type,Integer integral);

    /**
     * 初始化页面 积分设置查询
     * @return
     */
    Integer selectExchange();

    /**
     * 普通用户统计
     * @param type 类型  阅读 read  评论 comment 合计 count
     * @param order  排序  0 降序  1升序
     * @param timeStart 开始时间
     * @param timeEnd 结束时间
     * @param pageNum 当前页
     * @param pageSize 当前页记录数量
     * @return
     */
    List<OrdinaryUserVO>  ordinaryUser(String type,  Integer order ,  Long timeStart,  Long timeEnd,Integer pageNum,Integer pageSize,String networkId);

    /**
     * 特殊用户
     * @param type 类型  阅读 read  评论 comment 合计 count
     * @param order  排序  0 降序  1升序
     * @param timeStart 开始时间
     * @param timeEnd 结束时间
     * @param  pageNum 当前页
     * @param  pageSize 当前页记录数量
     * @return
     */
    List<SpecialUserVO>  specialUser( String type, Integer order , Long timeStart, Long timeEnd,Integer pageNum,Integer pageSize,String networkId);

    /**
     * 设置特殊处理类
     * @param integralService 用户自定义的处理类对象
     */
    void setIntegralService(IntegralService integralService);

}
