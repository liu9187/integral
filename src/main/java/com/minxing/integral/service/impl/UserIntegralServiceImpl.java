package com.minxing.integral.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.minxing.integral.common.bean.Integral;
import com.minxing.integral.common.bean.IntegralRecord;
import com.minxing.integral.common.bean.UserInfos;
import com.minxing.integral.common.pojo.vo.IntegralManagementVO;
import com.minxing.integral.common.pojo.vo.OrdinaryUserVO;
import com.minxing.integral.common.pojo.vo.SpecialUserVO;
import com.minxing.integral.common.util.HttpNetClientUtil;
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
    public Integer removeUserIntegralByUserId(UserInfos userIntegral) {
        //更新积分
        Integer integral = -1;
        Integer out = userIntegralMapper.removeUserIntegralByUserId( userIntegral );
        if (out > 0) {
            //反馈剩余积分信息
            integral = userIntegralMapper.selectIntegralByUserId( userIntegral.getUserId() );
        }
        return integral;
    }

    /**
     * 积分系统显示
     *
     * @param order
     * @return
     */
    @Override
    public List<IntegralManagementVO> queryList(String networkId, String nameStr, Integer order) {
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
            list = userIntegralMapper.queryList( networkId, name, order );

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

    //TODO 增加积分部分添加单独的接口供三方实现

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
            JSONObject json = JSONObject.parseObject( extParams );
                 Object categoryId=json.get("category_id");
            //对 categoryId 进行判断 是否为空 如果为空将要被拦截
            if ( categoryId.equals( null )|| (!categoryId.equals( null ) && categoryId.toString().length()==0)) {
                logger.error( "error is category_id null:" +categoryId);
                return false;
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
            //增加积分
            Integer res = userIntegralMapper.addIntegralByUserId( Integer.valueOf( userId ), integral.getIntegral().intValue() );
            if (1 != res) {
                logger.error( "add integral error" );
                return false;
            }
            //TODO 增加积分调用第三方接口
            try {
                String data_type = "integral";
                Integer integer = integral.getIntegral().intValue();
                String value = "integer";
                String user_id = userId;
                List<NameValuePair> urlParameters = new ArrayList<>();
                urlParameters.add( new BasicNameValuePair( "data_type", data_type ) );
                urlParameters.add( new BasicNameValuePair( "value", value ) );
                urlParameters.add( new BasicNameValuePair( "user_id", user_id ) );
                //调用接口
                String c = HttpNetClientUtil.doPut( urlParameters, Authorization, domain );
                Integer code = (Integer) JSONArray.parseObject( c ).get( "code" );
                //判断外部接口是否调用成功
                if (code != 200) {
                    logger.error( "error is doPut code:" + code );
                    return false;
                }
            } catch (Exception e1) {
                logger.error( "error is addIntegralByUserId" );
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
            logger.error( "event operation error" );
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

