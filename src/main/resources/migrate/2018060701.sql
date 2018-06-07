-- 事件类型表
CREATE TABLE IF NOT EXISTS `integral` (
  `id` int(11) NOT NULL,
  `type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '阅读, 转发，评论 等',
  `integral` int(11) DEFAULT NULL COMMENT '相应的类型',
  PRIMARY KEY (`id`)
);

-- 积分事件关联表
CREATE TABLE IF NOT EXISTS `integral_record` ( 
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `integral_id` int(11) DEFAULT NULL,
  `user_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` int(13) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

-- 设置兑换积分表
CREATE TABLE IF NOT EXISTS `integral_exchange` ( 
  `id` int(11) NOT NULL, 
  `integral_exchange` int(11) DEFAULT NULL, 
  `create_date` datetime DEFAULT NULL, 
  PRIMARY KEY (`id`) 
);

-- 事件表
CREATE TABLE IF NOT EXISTS `valid_event` ( 
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id', 
  `user_id` int(11) NOT NULL COMMENT '用户id', 
  `article_id` int(11) NOT NULL COMMENT '事件id', 
  `create_date` datetime NOT NULL, 
  PRIMARY KEY (`id`) 
);

INSERT INTO integral_exchange(id,integral_exchange,create_date) VALUES (1,200,NOW());
