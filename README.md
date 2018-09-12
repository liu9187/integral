## 0.0.1版本目标（2018年4月26日） ##
```
1、事件中心调用积分系统，增加积分接口。
2、提供web端积分统计功能，进行积分兑换。
3、提供普通用户统计以及特殊用户统计接口。
4、初始化创建表（integral、integral_exchange、integral_record、valid_event）
```

## 2018年6月7日 ##
```text
1、添加migrate工具

```

## 1.0.1 2018年8月7日 ##
```text
1、修改build脚本

```
## 1.0.2 2018年9月12日 ##
```
***增加***
1.勋值 显示 接口 selectMeritByUserId

***修改***
1.积分兑换接口 removeUserIntegralByUserId
   积分规则改变： 
        Ⅰ.积分满足兑换条件：积分兑换
        Ⅱ.积分不满足条件，但是积分和勋值的和满足
           条件:积分兑换，剩余部分勋值兑换
        Ⅲ.不满足兑换条件
2.增加积分接口 addIntegral
   积分规则改变：
        Ⅰ.普通用户：阅读、评论 增加积分
        Ⅱ.特殊用户：阅读、评论增加积分；转发增加勋值
3.积分勋值统计接口(原积分统计页面) queryList
    传入参数改变：
        Ⅰ.type 默认 积分类型 integral 
                     勋值类型 meritScore                 

***接口文档***
https://git.dehuinet.com/docs_group/api_docs


```
