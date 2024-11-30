package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//通过加一个跨域拦截器来 过滤相应，在响应头上加上请求
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");//允许所有源来跨域访问
        corsConfiguration.addAllowedHeader("*");//放行全部调用头
        corsConfiguration.addAllowedMethod("*");//放行所有方法
        corsConfiguration.setAllowCredentials(true);//允许跨域访问cookie

        //UrlBasedCorsConfigurationSource是CorsFilter的实现类
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(corsConfigurationSource);
    }
}
