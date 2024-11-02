package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 内容管理服务启动类
 */
@SpringBootApplication
@EnableSwagger2Doc //swagger接口文档
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
