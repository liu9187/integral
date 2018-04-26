package com.minxing.integral.common.bean;

import java.sql.Timestamp;

/**
 * Created by SZZ on 2017/5/10.
 */
public class Oauth2AccessToken {

    private Long accountId;
    private Timestamp expiredTime;

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
