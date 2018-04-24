package com.minxing.integral.dao;

import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户积分mapper
 * @author liucl
 * @date 2018-4-17
 */
@Component
@Mapper
public interface UserIntegralMapper {
    /**
     * 积分兑换
     * @param userIntegral
     * @return
     */
    @Update("UPDATE user_infos SET integral=integral-#{integral} WHERE user_id=#{userId}")
    Integer  removeUserIntegralByUserId(UserInfos userIntegral);

    /**
     * 积分管理 升序
     * @return IntegralManagementVO
     */
    @Select("SELECT u.`name`,ui.integral,dept.short_name FROM users u " +
            "LEFT JOIN  user_infos ui ON ui.user_id=u.id " +
            "LEFT JOIN  departments dept  ON dept.id=u.dept_id " +
            "ORDER BY ui.integral ")
   List<IntegralManagementVO>  queryListByASC();
    /**
     * 积分管理 降序
     * @return IntegralManagementVO
     */
    @Select("SELECT u.`name`,ui.integral,dept.short_name FROM users u " +
            "LEFT JOIN  user_infos ui ON ui.user_id=u.id " +
            "LEFT JOIN  departments dept  ON dept.id=u.dept_id " +
            "ORDER BY ui.integral DESC")
//    @Results(id = "learnMap", value = {
//            @Result(column = "integral", property = "integral", javaType = Long.class),
//            @Result(property = "short_name", column = "shortName", javaType = String.class),
//            @Result(property = "name", column = "name", javaType = String.class)
//    })
    List<IntegralManagementVO> queryListByDESC();

    /**
     * 积分设置
     * @param integralExchange
     * @return
     */
    @Update("update integral_exchange SET integral_exchange=#{integral_exchange},create_date=NOW() WHERE id=1")
    Integer updateIntegral(Integer integralExchange);

    /**
     * 通过userid 查询 积分
     * @param userId
     * @return
     */
    @Select("SELECT integral FROM user_integral WHERE user_id=#{userId}")
    Long queryIntegralByUserId(Integer userId);

    /**
     * 通过usrid增加积分
     * @param userIntegral
     * @return
     */
    @Update("UPDATE user_infos SET integral=integral+#{integral} WHERE user_id=#{userId}")
    Integer addIntegralByUserId(UserInfos userIntegral);

    /**
     * 根据事件查询对应时间的积分
     * @param type
     * @return integral
     */
    @Select("SELECT  integral FROM integral where type=#{type}")
    Long selectIntegral(String type);

    /**
     * 修改积分规则
     * 每次事件对应积分数
     * @param type
     * @return
     */
    @Update("UPDATE integral SET integral=6 WHERE type=#{type}")
    Integer updateIntegralByType(String type);
}

