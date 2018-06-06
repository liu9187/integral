package com.minxing.integral.dao;

import com.minxing.integral.common.bean.Integral;
import com.minxing.integral.common.bean.IntegralRecord;
import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.pojo.vo.OrdinaryUserVO;
import com.minxing.integral.common.pojo.vo.SpecialUserVO;
import com.minxing.integral.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * 积分兑换-更新积分
     * @param userIntegral
     * @return
     */
    @Update("UPDATE user_infos SET integral=integral-#{integral} WHERE user_id=#{userId} AND integral>=#{integral}")
    Integer  removeUserIntegralByUserId(UserInfos userIntegral);

    /**
     * 积分兑换-返回积分信息
     * @return
     */
    @Select("SELECT integral FROM user_infos  WHERE user_id=#{userId}")
    Integer selectIntegralByUserId(@Param("userId") Integer userId);

    /**
     * 积分管理 升序
     * @return IntegralManagementVO
     */
    @Select("SELECT u.id,u.`name`,IFNULL(ui.integral,0) AS integral,dept.short_name AS shortName FROM users u  \n" +
            " LEFT JOIN  user_infos ui ON ui.user_id=u.id   \n" +
            " LEFT JOIN  departments dept  ON dept.id=u.dept_id   \n" +
            " where u.role_code = 1 and u.actived = 1 and u.deleted_at > now() and u.network_id = #{networkId} "+
            " ORDER BY ui.integral ")
   List<IntegralManagementVO>  queryListByASC(@Param("networkId") String networkId,@Param("name") String name );
    /**
     * 积分管理 降序
     * @return IntegralManagementVO
     */
    @Select("SELECT u.id,u.`name`,IFNULL(ui.integral,0) AS integral,dept.short_name AS shortName FROM users u  \n" +
            "LEFT JOIN  user_infos ui ON ui.user_id=u.id   \n" + "LEFT JOIN  departments dept  ON dept.id=u.dept_id   \n" +
            " where u.role_code = 1 and u.actived = 1 and u.deleted_at > now() and u.network_id = #{networkId} "+
            "ORDER BY ui.integral  DESC")
    List<IntegralManagementVO> queryListByDESC(@Param("networkId") String networkId ,@Param("name") String name );

    /**
     * 积分设置
     * @param integralExchange
     * @return
     */
    @Update("update integral_exchange SET integral_exchange=#{integralExchange},create_date=NOW() WHERE id=1")
    Integer updateIntegral(@Param("integralExchange") Long integralExchange);

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
    @Insert("INSERT INTO `integral_record` (`integral_id`, `user_id`, `create_date`) VALUES (#{integralId}, #{userId}, #{createDate});")
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
     * 查询是否为普通用户
     * @param userId
     * @param itemId
     * @return
     */
    @Select( "SELECT user_id FROM  roles WHERE role_item_id=#{itemId} AND user_id=#{userId}" )
    String selectOrdinaryUser(@Param("userId")String userId,@Param("itemId") Integer itemId);

    /**
     * 普通用户统计
     * @param type 类型  阅读 read  评论 comment 合计 count
     * @param order  排序  0 降序  1升序
     * @param timeStart 开始时间
     * @param timeEnd 结束时间
     * @return
     */
 @SelectProvider(type =IntegralSqlBuilder.class,method = "ordinaryUser")
 List<OrdinaryUserVO>  ordinaryUser(@Param("itemId") Integer itemId,@Param("type")String type, @Param("order") Integer order ,@Param("timeStart") Long timeStart,@Param("timeEnd") Long timeEnd,@Param("networkId")  String networkId,@Param("name") String name);

    /**
     * 积分管理页面
     * @param networkId
     * @param name
     * @return
     */
    @SelectProvider(type =IntegralSqlBuilder.class,method = "queryList")
    List<IntegralManagementVO> queryList(@Param("networkId") String networkId ,@Param("name") String name , @Param("order") Integer order);
    /**
     * 特殊用户
     * @param type 类型  阅读 read  评论 comment 合计 count
     * @param order  排序  0 降序  1升序
     * @param timeStart 开始时间
     * @param timeEnd 结束时间
     * @return
     */
 @SelectProvider(type =IntegralSqlBuilder.class,method = "SpecialUser")
    List<SpecialUserVO>  SpecialUser(@Param("itemId") Integer itemId,@Param("type")String type, @Param("order") Integer order , @Param("timeStart") Long timeStart, @Param("timeEnd") Long timeEnd,@Param("networkId")  String networkId,@Param("name") String name);

    class IntegralSqlBuilder{
        Logger logger = LoggerFactory.getLogger(IntegralSqlBuilder.class);

        /**
         * 普通用户
         * @param itemId
         * @param type
         * @param order
         * @param timeStart
         * @param timeEnd
         * @param networkId
         * @param name
         * @return
         */
        public String ordinaryUser(@Param("itemId") final Integer itemId,@Param("type") final  String type, @Param("order") final  Integer order,@Param("timeStart") final Long timeStart,@Param("timeEnd") final Long timeEnd,@Param("networkId") final String networkId ,@Param("name") final String name){
            StringBuffer sql =new StringBuffer();
            try {
                sql.append("SELECT u.`name`, " +
                        "       IF (SUM(ir.integral_id=1)>0,SUM(ir.integral_id=1),0) AS 'read' ," +
                        "       IF(SUM(ir.integral_id=2)>0,SUM(ir.integral_id=2),0)  AS 'comment'," +
                        "       IF(SUM(ir.integral_id=1)>0,SUM(ir.integral_id=1),0)+IF(SUM(ir.integral_id=2)>0,SUM(ir.integral_id=2),0) AS count   " +
                        "    FROM  users u  " +
                        "    LEFT JOIN integral_record ir ON ir.user_id=u.id  " +
                        "    WHERE u.role_code = 1 and u.actived = 1 and u.deleted_at > now() and u.network_id = #{networkId} and  u.id not IN(SELECT user_id FROM  roles WHERE role_item_id=#{itemId}) " );

                if (!StringUtil.isNull(type) && null !=order){
                    //判断开始时间是否为null
                    if (null !=timeStart){
                        sql.append("and  ir.create_date>#{timeStart} ");
                    }
                    //判断结束时间是否为null
                    if (null !=timeEnd){
                        sql.append("AND ir.create_date<#{timeEnd} ");
                    }
                    //判断name是否为null
                    if (StringUtils.isNotEmpty(name)){
                        sql.append(" AND (u.`name` LIKE #{name} OR u.pinyin LIKE #{name}  OR u.initialism LIKE #{name} ) ");
                    }
                     //根据阅读次数排序
                    if (type.equals("read")){
                        if (order==1){
                            sql.append("GROUP BY u.id   ORDER BY ifnull(SUM(ir.integral_id = 1),0) ASC ,u.pinyin ASC");
                        }else if(order==0){
                            sql.append("GROUP BY u.id  ORDER BY ifnull(SUM(ir.integral_id = 1),0)   DESC ,u.pinyin ASC");
                        }
                    }
                    //根据评论次数排序
                    if (type.equals("comment")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 2),0) ASC ,u.pinyin ASC");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 2),0) DESC ,u.pinyin ASC");
                        }
                    }
                    //根据合计数排序
                    if (type.equals("count")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 1),0)+ifnull(SUM(ir.integral_id = 2),0)  ,u.pinyin ASC");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 1),0)+ifnull(SUM(ir.integral_id = 2),0) DESC ,u.pinyin ASC");
                        }
                    }
                }
            }catch (Exception e){
                //普通用户sql出现错误
                logger.error("error is mapper ordinaryUser: "+e);
               // System.out.print("error sql is  ordinaryUser");
            }
              //输出查询普通用户的sql语句
             logger.info("查询sql=="+sql.toString());
            //System.out.println("查询sql=="+sql.toString());
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
        public String SpecialUser( @Param("itemId") final Integer itemId,@Param("type") final  String type, @Param("order") final  Integer order,@Param("timeStart") final Long timeStart,@Param("timeEnd") final Long timeEnd,@Param("networkId") final String networkId,@Param("name") final String name ){
            StringBuffer sql =new StringBuffer();
            try {
                sql.append("SELECT u.`name`, " +
                        "       IF (SUM(ir.integral_id=1)>0,SUM(ir.integral_id=1),0) AS 'read' ," +
                        "       IF(SUM(ir.integral_id=2)>0,SUM(ir.integral_id=2),0)  AS 'comment'," +
                        "       IF(SUM(ir.integral_id=3)>0,SUM(ir.integral_id=3),0)  AS 'forward'," +
                        "       IF(SUM(ir.integral_id=3)>0,SUM(ir.integral_id=3),0)+IF(SUM(ir.integral_id=2)>0,SUM(ir.integral_id=2),0) AS count1, " +
                        "       IF (SUM(ir.integral_id=1)>0,SUM(ir.integral_id=1),0)+IF(SUM(ir.integral_id=2)>0,SUM(ir.integral_id=2),0)+IF(SUM(ir.integral_id=3)>0,SUM(ir.integral_id=3),0) AS count2 " +
                        "    FROM  users u  " +
                        "    LEFT JOIN integral_record ir ON ir.user_id=u.id " +
                        "    WHERE u.role_code = 1 and u.actived = 1 and u.deleted_at > now() and u.network_id = #{networkId} and  u.id IN(SELECT user_id FROM  roles WHERE role_item_id=#{itemId}) " );

                if (!StringUtil.isNull(type) && null !=order){
                    //判断开始时间是否为null
                    if (null !=timeStart){
                        sql.append("and  ir.create_date>#{timeStart} ");
                    }
                    //判断结束时间是否为null
                    if (null !=timeEnd){
                        sql.append("AND ir.create_date<#{timeEnd} ");
                    }
                    //判断是否有name
                    if (StringUtils.isNotEmpty(name)){
                        sql.append(" AND (u.`name` LIKE #{name} OR u.pinyin LIKE #{name}  OR u.initialism LIKE #{name} ) ");
                    }
                    //根据阅读次数排序
                    if (type.equals("read")){
                        if (order==1){
                            sql.append("GROUP BY u.id  ORDER BY ifnull(SUM(ir.integral_id = 1),0) ,u.pinyin ASC");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 1),0) DESC ,u.pinyin ASC");
                        }
                    }

                    //根据评论次数排序
                    if (type.equals("comment")){
                        if (order==1){
                            sql.append("GROUP BY u.id  ORDER BY ifnull(SUM(ir.integral_id = 2),0) ,u.pinyin ASC");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 2),0) DESC ,u.pinyin ASC");
                        }
                    }
                    //根据转发次数排序
                    if (type.equals("forward")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 3),0) ,u.pinyin ASC");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 3),0) DESC ,u.pinyin ASC");
                        }
                    }
                    //根据评论+转发数排序
                    if (type.equals("count1")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 2),0)+ifnull(SUM(ir.integral_id = 3),0) ,u.pinyin ASC");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 2),0)+ifnull(SUM(ir.integral_id = 3),0) DESC ,u.pinyin ASC");
                        }
                    }
                    //根据总合计数排序
                    if (type.equals("count2")){
                        if (order==1){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 1),0)+ifnull(SUM(ir.integral_id = 2),0)+ifnull(SUM(ir.integral_id = 3),0) ,u.pinyin ASC");
                        }else if(order==0){
                            sql.append("GROUP BY u.id ORDER BY ifnull(SUM(ir.integral_id = 1),0)+ifnull(SUM(ir.integral_id = 2),0)+ifnull(SUM(ir.integral_id = 3),0) DESC ,u.pinyin ASC");
                        }
                    }
                }
            }catch (Exception e){
                //特殊用户sql出现错误日志
                logger.error("error is mapper SpecialUser:"+e);
            }
            logger.info("查询sql=="+sql.toString());
            //System.out.println("查询sql=="+sql.toString());
            return sql.toString();
        }

        /**
         * 积分管理页面
         * @param networkId
         * @param name
         * @return
         */
        public String queryList(@Param("networkId") final String networkId,@Param("name") final String name , @Param("order") final  Integer order){
            StringBuffer sql =new StringBuffer();
            try{
              sql.append( "SELECT u.id,u.`name`,IFNULL(ui.integral,0) AS integral,dept.short_name AS shortName FROM users u  \n" +
                      " LEFT JOIN  user_infos ui ON ui.user_id=u.id   \n" +
                      " LEFT JOIN  departments dept  ON dept.id=u.dept_id   \n" +
                      " where u.role_code = 1 and u.actived = 1 and u.deleted_at > now() and u.network_id = #{networkId} "
                      );
                //判断是否有name
                if (StringUtils.isNotEmpty(name)){
                    sql.append(" AND (u.`name` LIKE #{name} OR u.pinyin LIKE #{name}  OR u.initialism LIKE #{name} ) ");
                }
                  //判断是升序还是降序
                if (order==1){
                    sql.append(" ORDER BY ui.integral ,u.pinyin ASC");
                }
                else if (order==0){
                    sql.append(" ORDER BY ui.integral DESC ,u.pinyin ASC");
                }
            }catch (Exception e){
                //积分管理升序用户sql出现错误日志
                logger.error("error is mapper queryListByASC:"+e);
            }
            logger.info("查询sql=="+sql.toString());
            return sql.toString();
        }

    }
}

