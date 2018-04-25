package com.minxing.integral.dao;

import com.minxing.integral.common.bean.Integral;
import com.minxing.integral.common.bean.IntegralRecord;
import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.pojo.vo.OrdinaryUserVO;
import com.minxing.integral.common.pojo.vo.SpecialUserVO;
import com.minxing.integral.common.util.ServletUtil;
import com.minxing.integral.common.util.StringUtil;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;
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
    @Update("UPDATE user_infos SET integral=integral-#{integral} WHERE user_id=#{userId} AND integral>#{integral}")
    Integer  removeUserIntegralByUserId(UserInfos userIntegral);

    /**
     * 积分管理 升序
     * @return IntegralManagementVO
     */
    @Select("SELECT u.id,u.`name`,ui.integral,dept.short_name FROM users u " +
            "LEFT JOIN  user_infos ui ON ui.user_id=u.id " +
            "LEFT JOIN  departments dept  ON dept.id=u.dept_id " +
            "ORDER BY ui.integral ")
   List<IntegralManagementVO>  queryListByASC();
    /**
     * 积分管理 降序
     * @return IntegralManagementVO
     */
    @Select("SELECT u.id,u.`name`,ui.integral,dept.short_name FROM users u " +
            "LEFT JOIN  user_infos ui ON ui.user_id=u.id " +
            "LEFT JOIN  departments dept  ON dept.id=u.dept_id " +
            "ORDER BY ui.integral DESC")
    List<IntegralManagementVO> queryListByDESC();

    /**
     * 积分设置
     * @param integralExchange
     * @return
     */
    @Update("update integral_exchange SET integral_exchange=#{integral_exchange},create_date=NOW() WHERE id=1")
    Integer updateIntegral(Integer integralExchange);

    /**
     * 通过userid增加积分
     * @return
     */
    @Update("UPDATE user_infos SET integral=integral+#{integrals} WHERE user_id=#{userId};")
    Integer addIntegralByUserId(@Param("userId") Integer userId, @Param("integrals") Integer integrals);

    /**
     * 根据事件查询对应时间的积分
     * @param type
     * @return integral
     */
    @Select("SELECT id, type, integral FROM integral where type=#{type}")
    Integral selectIntegral(String type);

    /**
     * 修改积分规则
     * 每次事件对应积分数
     * @param type
     * @return
     */
    @Update("UPDATE integral SET integral=#{integral} WHERE type=#{type}")
    Integer updateIntegralByType(@Param("type") String type,@Param("integral") Integer integral);


    /**
     * 新增用户事件
     * @param integralRecord
     * @return
     */
    @Insert("INSERT INTO `integral_record` (`integral_id`, `user_id`, `create_date`) VALUES (#{integralId}, #{userId}, #{createDate});\n")
    Integer insertIntegralRecord(IntegralRecord integralRecord);

    /**
     * 新增一条有效记录
     * @param userId
     * @param articleId
     * @param createDate
     * @return
     */
    @Insert("INSERT INTO valid_event (user_id, article_id, create_date) VALUES (#{userId}, #{articleId}, #{createDate});")
    Integer addValidEvent(@Param("userId") Integer userId, @Param("articleId") Integer articleId, @Param("createDate") Date createDate);

    /**
     * 查看是否记录有效事件
     * @param userId
     * @param articleId
     * @return
     */
    @Select("SELECT COUNT(id) FROM `valid_event` WHERE user_id = #{userId} AND article_id = #{articleId};")
    Integer findIsValid(@Param("userId") Integer userId, @Param("articleId") Integer articleId);

    /**
     * 初始化页面 积分设置查询
     * @return
     */
    @Select("SELECT integral_exchange FROM integral_exchange  WHERE id=1")
    Integer selectExchange();

    /**
     * 普通用户统计
     * @param type 类型  阅读 read  评论 comment 合计 count
     * @param order  排序  0 降序  1升序
     * @param timeStart 开始时间
     * @param timeEnd 结束时间
     * @return
     */
 @SelectProvider(type =IntegralSqlBuilder.class,method = "ordinaryUser")
 List<OrdinaryUserVO>  ordinaryUser(@Param("type")String type, @Param("order") Integer order ,@Param("timeStart") Long timeStart,@Param("timeEnd") Long timeEnd);
    /**
     * 特殊用户
     * @param type 类型  阅读 read  评论 comment 合计 count
     * @param order  排序  0 降序  1升序
     * @param timeStart 开始时间
     * @param timeEnd 结束时间
     * @return
     */
 @SelectProvider(type =IntegralSqlBuilder.class,method = "SpecialUser")
    List<SpecialUserVO>  SpecialUser(@Param("type")String type, @Param("order") Integer order , @Param("timeStart") Long timeStart, @Param("timeEnd") Long timeEnd);

    class IntegralSqlBuilder{

        public String ordinaryUser(@Param("type") final  String type, @Param("order") final  Integer order,@Param("timeStart") final Long timeStart,@Param("timeEnd") final Long timeEnd){
            StringBuffer sql =new StringBuffer();
            try {
                sql.append("SELECT u.`name`, SUM(ir.integral_id=1) AS 'read' ,SUM(ir.integral_id=2) AS 'comment',SUM(ir.integral_id=1)+SUM(ir.integral_id=2) AS count FROM  users u  " +
                        "LEFT JOIN integral_record ir ON ir.user_id=u.id  " +
                        "WHERE  1=1 " );

                if (!StringUtil.isNull(type) && null !=order){
                    //判断开始时间是否为null
                    if (null !=timeStart){
                        sql.append("and  ir.create_date>#{timeStart}");
                    }
                    //判断结束时间是否为null
                    if (null !=timeEnd){
                        sql.append("AND ir.create_date<#{timeEnd} ");
                    }
                     //根据阅读次数排序
                    if (type.equals("read")){
                        if (order==1){
                            sql.append("GROUP BY u.id   ORDER BY SUM(ir.integral_id=1)");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=1) DESC");
                        }
                    }
                    //根据评论次数排序
                    if (type.equals("comment")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=2)");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=2) DESC");
                        }
                    }
                    //根据合计数排序
                    if (type.equals("count")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=1)+SUM(ir.integral_id=2)");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=1)+SUM(ir.integral_id=2) DESC");
                        }
                    }
                }
            }catch (Exception e){
                e.getMessage();
                System.out.print("error sql is  ordinaryUser");
            }
            System.out.println("查询sql=="+sql.toString());
            return sql.toString();
        }

        /**
         * 特殊用户
         * @param type
         * @param order
         * @param timeStart
         * @param timeEnd
         * @return
         */
        public String SpecialUser(@Param("type") final  String type, @Param("order") final  Integer order,@Param("timeStart") final Long timeStart,@Param("timeEnd") final Long timeEnd){
            StringBuffer sql =new StringBuffer();
            try {
                sql.append("SELECT u.`name`, SUM(ir.integral_id=1) AS 'read' ,SUM(ir.integral_id=2) AS 'comment',SUM(ir.integral_id=3) AS 'forward',SUM(ir.integral_id=3)+SUM(ir.integral_id=2) AS count1,SUM(ir.integral_id=1)+SUM(ir.integral_id=2)+SUM(ir.integral_id=3) AS count2 FROM  users u  " +
                           "LEFT JOIN integral_record ir ON ir.user_id=u.id  " +
                          "WHERE  1=1  " );

                if (!StringUtil.isNull(type) && null !=order){
                    //判断开始时间是否为null
                    if (null !=timeStart){
                        sql.append("and  ir.create_date>#{timeStart}");
                    }
                    //判断结束时间是否为null
                    if (null !=timeEnd){
                        sql.append("AND ir.create_date<#{timeEnd} ");
                    }
                    //根据阅读次数排序
                    if (type.equals("read")){
                        if (order==1){
                            sql.append("GROUP BY u.id  ORDER BY SUM(ir.integral_id=1)");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=1) DESC");
                        }
                    }

                    //根据评论次数排序
                    if (type.equals("comment")){
                        if (order==1){
                            sql.append("GROUP BY u.id  ORDER BY SUM(ir.integral_id=2)");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=2) DESC");
                        }
                    }
                    //根据转发次数排序
                    if (type.equals("forward")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=3)");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=3) DESC");
                        }
                    }
                    //根据评论+转发数排序
                    if (type.equals("count1")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=2)+SUM(ir.integral_id=3)");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=2)+SUM(ir.integral_id=3) DESC");
                        }
                    }
                    //根据总合计数排序
                    if (type.equals("count2")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=1)+SUM(ir.integral_id=2)+SUM(ir.integral_id=3)");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY SUM(ir.integral_id=1)+SUM(ir.integral_id=2)+SUM(ir.integral_id=3) DESC");
                        }
                    }
                }
            }catch (Exception e){
                e.getMessage();
                System.out.print("error sql is  SpecialUser");
            }
            System.out.println("查询sql=="+sql.toString());
            return sql.toString();
        }
    }
}

