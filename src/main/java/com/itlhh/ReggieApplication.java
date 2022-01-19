package com.itlhh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author lhh
 * @Date 2022/1/12 15:34
 */
@Slf4j
@SpringBootApplication
// 开启组件扫描(扫描过滤器配置的@WebFilter注解)
@ServletComponentScan
//开启事物
@EnableTransactionManagement
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功...");
    }
}
