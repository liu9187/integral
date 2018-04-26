package com.minxing.integral.dao;

import com.minxing.integral.common.bean.Oauth2AccessToken;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * Created by SZZ on 2017/5/10.
 */
public interface UserMapper {

    @Select("SELECT id FROM users WHERE network_id = #{networkId} AND account_id = #{accountId} AND actived=1 limit 1")
    Integer findUidByAccountIdAndNetWorkId(@Param("accountId") Long accountId, @Param("networkId") Integer networkId);

    //    @Results({@Result(column = "account_id", property = "accountId"), @Result(column = "expires_at", property = "expiredTime")})
    @Select("SELECT account_id as accountId ,expires_at as expiredTime from oauth2_access_tokens " +
            "WHERE token = #{token} " +
            "ORDER BY expires_at DESC " +
            "LIMIT 1")
    Oauth2AccessToken findAccountByToken(@Param("token") String token);

}
