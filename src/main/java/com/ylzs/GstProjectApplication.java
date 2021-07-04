package com.ylzs;

import com.ylzs.common.cache.InitUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *本程序启动类。实现CommandLineRunner的run方法后，可在程序启动后执行一些初始化工作
 *本程序启动类。实现CommandLineRunner的run方法后，可在程序启动后执行一些初始化工作
 */
@SpringBootApplication
@MapperScan("com.ylzs.dao")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@ServletComponentScan
public class GstProjectApplication implements CommandLineRunner, ApplicationContextAware {
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(GstProjectApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;    }

    @Override
    public void run(String... args) throws Exception {
        //把 applicationContext 传递过来，可以执行一些启动的初始化工作
        InitUtil.applicationContext = applicationContext;
        InitUtil.staticDataCacheInit(applicationContext);
    }

}
