package com.minxing.integral;

import com.minxing365.dbschema.migrate.MigrateTool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by SuZZ on 2018/1/4.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.minxing.integral")
@EnableTransactionManagement
@ServletComponentScan(basePackages = "com.minxing.integral.filter")
public class StartUp {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

        if (args != null && args.length > 0 && "update".equals(args[0])) {

            //migrate list
            List<String> migrateList = new ArrayList<>();
            migrateList.add("2018060701.sql");

            String configPath = System.getProperty("spring.config.location");
            if (null == configPath) {
                System.err.println("未指定配置文件！");
                System.exit(0);
            }
            Properties properties = new Properties();
            properties.load(new FileReader(configPath));
            String driverClass = properties.getProperty("db.driverClass");
            String dburl = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            MigrateTool migrateTool = MigrateTool.getInstance(driverClass, dburl, username, password, "mx_integral", migrateList);
            migrateTool.update();
        } else {
            SpringApplication.run(StartUp.class, args);
        }

    }

}
