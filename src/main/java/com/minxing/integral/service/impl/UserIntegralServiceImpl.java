package com.minxing.integral.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.minxing.integral.common.bean.Integral;
import com.minxing.integral.common.bean.IntegralRecord;
import com.minxing.integral.common.bean.Person;
import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.pojo.vo.OrdinaryUserVO;
import com.minxing.integral.common.pojo.vo.SpecialUserVO;
import com.minxing.integral.common.util.IntegralHelper;
import com.minxing.integral.dao.UserIntegralMapper;
import com.minxing.integral.service.IntegralService;
import com.minxing.integral.service.UserIntegralService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.minxing.integral.service.IntegralService.OCU_ARTICLE_FORWARD;

@Service
public class UserIntegralServiceImpl implements UserIntegralService {

    Logger logger = LoggerFactory.getLogger( UserIntegralServiceImpl.class );

    @Autowired
    private UserIntegralMapper userIntegralMapper;

    private IntegralService integralService;

    @Value("${event_type_is_valid}")
    private String isValidEvent;
    //特殊用户群组id
    @Value("${role.item.id}")
    private Integer itemId;
    //特殊用户群组id
    @Value("${Authorization}")
    private String Authorization;
    @Value("${mx.domain}")
    private String domain;

    /**
     * 积分兑换
     *
     * @param userIntegral
     * @return out
     */
    @Override
    @Transactional
    public String removeUserIntegralByUserId(UserInfos userIntegral) {
        //查看 数据库中的 积分和勋值
        UserInfos userInfos = userIntegralMapper.selectMeritByUserId( userIntegral.getUserId() );
        JSONObject jsonObject = new JSONObject();

        if (null != userInfos) {
            //判断 积分或者勋值是否满足要求
            //积分值
            Long integral = userInfos.getIntegral();
            //勋值
            Long meritScore = userInfos.getMeritScore();
            try {
                //积分数值大于或者等于 兑换值积分
                if (integral >= userIntegral.getIntegral()) {
                    UserInfos user = new UserInfos();
                    user.setUserId( userIntegral.getUserId() );
                    user.setIntegral( integral - userIntegral.getIntegral() );
                    //兑换积分
                    userIntegralMapper.removeUserIntegralByUserId( user );
                    jsonObject.put( "message", "积分满足，兑换成功" );
                    //判断如果积分小于兑换值
                } else {
                    //如果勋值和积分的和大于或者兑换值
                    if (integral + meritScore >= userIntegral.getIntegral()) {
                        UserInfos user = new UserInfos();
                        //兑换的积分
                        user.setIntegral( Long.valueOf( 0 ) );
                        //兑换的勋值
                        user.setMeritScore( meritScore - userIntegral.getIntegral() - integral );
                        user.setUserId( userIntegral.getUserId() );
                        //积分兑换
                        userIntegralMapper.removeUserIntegralByUserId( user );
                        //勋值兑换
                        userIntegralMapper.removeUsermByUserId( user );
                        jsonObject.put( "message", "积分不足，积分和勋值兑换" );

                    } else {
                        //如果勋值和积分的和小于兑换值
                        jsonObject.put( "message", "不满足兑换要求" );
                    }

                }
            } catch (Exception e) {
                logger.error( "<<<<<<积分或者勋值数值为null，积分=" + integral + "; 勋值=" + meritScore );
            }

        }
        UserInfos result = userIntegralMapper.selectMeritByUserId( userIntegral.getUserId() );
        jsonObject.put( "integral", result.getIntegral() );
        jsonObject.put( "meritScore", result.getMeritScore() );

        return jsonObject.toJSONString();
    }

    /**
     * 积分系统显示
     * @param type  需要排序的 数据类型 meritScore 积分  ；默认 integral 积分
     * @param order
     * @return
     */
    @Override
    public List<IntegralManagementVO> queryList(String networkId, String nameStr, Integer order, String type) {
        List<IntegralManagementVO> list = new ArrayList<>();
        try {
//            if (order==1) {
//                list = userIntegralMapper.queryListByASC(networkId);
//            } else {
//                // 默认降序
//                list = userIntegralMapper.queryListByDESC(networkId);
//            }
            String name = null;
            if (StringUtils.isNotEmpty( nameStr )) {
                name = "%" + nameStr + "%";
            }
            logger.info( "name:" + name );
            list = userIntegralMapper.queryList( networkId, name, order, type );

        } catch (Exception e) {
            logger.error( "The error is queryList" + e );
        }
        return list;
    }

    /**
     * 积分设置
     *
     * @param integralModification
     * @return
     */
    @Override
    @Transactional
    public Integer updateIntegral(Long integralModification) {
        return userIntegralMapper.updateIntegral( integralModification );
    }


    /**
     * 增加积分
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public Boolean addIntegralByUserId(String userId, String actionType, String extParams) {

        try {
              if (null==extParams||"".equals( extParams )){
                  logger.error( "<<<<<<<<<<扩展参数为null" );
                   return false;
              }
            JSONObject json = JSONObject.parseObject( extParams );
//            //对 categoryId 进行判断 是否为空 如果为空将要被拦截
            JSONArray categoryId = (JSONArray) json.get( "category_id" );
            if (categoryId == null || categoryId.size() == 0) {
                logger.info( "error is category_id :  " + categoryId );
                return true;
            }
            //判断是否是有效事件(只有阅读)
            if (isValidEvent.equals( actionType )) {
                Object articleId = json.get( "article_id" );
                Integer res = userIntegralMapper.findIsValid( Integer.valueOf( userId ), Integer.valueOf( articleId.toString() ) );
                if (res > 0) {
                    logger.info( "userId" + userId + "and articleId" + articleId + "is not valid event" );
                    return true;
                }
                //新增记录 下次阅读为无效事件
                Integer ins = userIntegralMapper.addValidEvent( Integer.valueOf( userId ), Integer.valueOf( articleId.toString() ), new Date() );
                if (1 != ins) {
                    logger.error( "add valid event error" );
                    return false;
                }

            }
            //根据事件的类型查出对应积分数据
            Integral integral = userIntegralMapper.selectIntegral( actionType );
            if (integralService != null) {
                integral.setIntegral( integralService.calculate( actionType, extParams ) );
            }
            if (null == integral) {
                logger.error( "integral number error" );
                return false;
            }
            //判断user是不是普通用户 如果是普通用户转发不加积分
            String isnull = userIntegralMapper.selectOrdinaryUser( userId, itemId );
            if (StringUtils.isEmpty( isnull ) && actionType.equals( OCU_ARTICLE_FORWARD )) {
                logger.info( "this user is OrdinaryUser:  userId=" + userId + "--------------" + "itemId=" + itemId );
            } else {
                Long userIntegral = null;
                Long meritScore = null;
                //查询用户的积分情况
                //增加积分之前用户积分和勋值
                //  userIntegral = userIntegralMapper.selectIntegralByUserId( Integer.valueOf( userId ) );
                UserInfos userInfos1 = userIntegralMapper.selectMeritByUserId( Integer.valueOf( userId ) );
                userIntegral = userInfos1.getIntegral();
                meritScore = userInfos1.getMeritScore();
                logger.info( "start: userIntegral -----------" + userIntegral );
                if (null == userInfos1) {
                    logger.error( "error is selectMeritByUserId, userIntegral==" + userIntegral + ";meritScore==" + meritScore );
                    return false;
                }
                //数据类型
                String data_type = null;
                //用户积分总数
                String integer = null;
                //判断是不是转发
                if (actionType.equals( OCU_ARTICLE_FORWARD )) {
                    //增加勋值
                    data_type = "merit_score";
                    Long merit_sum = meritScore + integral.getIntegral().intValue();
                    logger.info( "增加以后的勋值为 merit_sum==" + merit_sum );
                    integer = String.valueOf( meritScore + integral.getIntegral().intValue() );
                } else {
                    //增加积分

                    Long integral_sum = integral.getIntegral().intValue() + userIntegral;
                    //增加之后用户积分
                    logger.info( "增加之后的用户积分 integral_sum -----------" + integral_sum);
                    data_type = "integral";
                    integer = String.valueOf( integral_sum);
                }


                //TODO 增加积分调用Ruby接口
                try {
//                    //数据类型
//                     data_type = "integral";
//                    //用户积分总数
//                     integer = userIntegral;
                    //  String value = integer.toString();
                    //用户
                    String user_id = userId;
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add( new BasicNameValuePair( "data_type", data_type ) );
                    urlParameters.add( new BasicNameValuePair( "value", integer ) );
                    urlParameters.add( new BasicNameValuePair( "user_id", user_id ) );
                    //调用接口需要的参数
                    logger.info( "Ruby interface call parameters: data_type=" + data_type + "-----value=" + integer + "-----user_id=" + user_id );
                    //事件加入环形缓冲区
                    Person person = new Person();
                    person.setAuth( Authorization );
                    person.setDomain( domain );
                    person.setUrlParameters( urlParameters );
                    //接口调用
                    IntegralHelper.add( person );
                } catch (Exception e1) {
                    logger.error( "Ruby interface call exception", e1 );
                }

            }

            //记录此次事件
            IntegralRecord integralRecord = new IntegralRecord();
            integralRecord.setIntegralId( integral.getId() );
            integralRecord.setUserId( userId );
            integralRecord.setCreateDate( System.currentTimeMillis() / 1000 );

            Integer rest = userIntegralMapper.insertIntegralRecord( integralRecord );
            if (1 != rest) {
                logger.error( "add integral_record error" );
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error( "event operation error", e );
            return false;
        }
        return true;
    }

    /**
     * 修改积分规则
     * 每次事件对应积分数
     *
     * @param type
     * @return
     */
    @Override
    public Integer updateIntegralByType(String type, Integer integral) {
        return userIntegralMapper.updateIntegralByType( type, integral );
    }

    /**
     * 初始化
     *
     * @return
     */
    @Override
    public Integer selectExchange() {
        return userIntegralMapper.selectExchange();
    }

    /**
     * 查询积分 和 勋值
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfos selectMeritByUserId(Integer userId) {
        return userIntegralMapper.selectMeritByUserId( userId );
    }

    /**
     * 普通用户
     *
     * @param type      类型  阅读 read  评论 comment 合计 count
     * @param order     排序  0 降序  1升序
     * @param timeStart 开始时间
     * @param timeEnd   结束时间
     * @param pageNum   当前页
     * @param pageSize  当前页记录数量
     * @return
     */
    @Override
    public List<OrdinaryUserVO> ordinaryUser(String type, Integer order, Long timeStart, Long timeEnd, Integer pageNum, Integer pageSize, String networkId, String nameStr) {
        String name = null;
        if (StringUtils.isNotEmpty( nameStr )) {
            name = "%" + nameStr + "%";
        }
        logger.info( "name:" + name );
        //分页插件
        PageHelper.startPage( pageNum, pageSize );
        return userIntegralMapper.ordinaryUser( itemId, type, order, timeStart, timeEnd, networkId, name );
    }

    /**
     * 特殊用户
     *
     * @param type      类型  阅读 read  评论 comment 合计 count
     * @param order     排序  0 降序  1升序
     * @param timeStart 开始时间
     * @param timeEnd   结束时间
     * @param pageNum   当前页
     * @param pageSize  当前页记录数量
     * @return
     */
    @Override
    public List<SpecialUserVO> specialUser(String type, Integer order, Long timeStart, Long timeEnd, Integer pageNum, Integer pageSize, String networkId, String nameStr) {
        String name = null;
        if (StringUtils.isNotEmpty( nameStr )) {
            name = "%" + nameStr + "%";
        }
        logger.info( "name:" + name );
        //分页插件
        PageHelper.startPage( pageNum, pageSize );
        return userIntegralMapper.SpecialUser( itemId, type, order, timeStart, timeEnd, networkId, name );
    }

    /**
     * 支持特殊处理类
     *
     * @param integralService
     */
    public void setIntegralService(IntegralService integralService) {
        this.integralService = integralService;
    }

}

