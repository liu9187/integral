package com.minxing.integral.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages="com.minxing.integral.dao", sqlSessionFactoryRef = "sqlSessionFactory")
public class DatasourceConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        // 数据库基础配置
        config.setDriverClassName(env.getProperty("db.driverClass"));
        config.setAutoCommit(false);
        config.setJdbcUrl(env.getProperty("db.url"));
        config.setUsername(env.getProperty("db.username"));
        config.setPassword(env.getProperty("db.password"));
        // 空闲超时时间
        config.setIdleTimeout(60000);
        // 连接超时时间
        config.setConnectionTimeout(30000);
        // 连接测试语句
        config.setConnectionTestQuery("SELECT 1");
        // 连接初始化语句
        config.setConnectionInitSql("SELECT 1");
        // 最大连接数
        config.setMaximumPoolSize(Integer.valueOf(env.getProperty("db.maximumPoolSize")));
        // 最小空闲数
        config.setMinimumIdle(1);
        // 非只读数据库
        config.setReadOnly(false);
        // 应参照mysql wait_timeout值,短1分钟左右
        config.setMaxLifetime(20000);
        return new HikariDataSource(config);
    }


    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }

}
