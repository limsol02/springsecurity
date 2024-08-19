package com.cos.security1.config;


import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MustacheViewResolver resolver = new MustacheViewResolver();

        resolver.setCharset("UTF-8");
        resolver.setContentType("text/html;charset=UTF-8"); // 내가 던지는 데이터는 html이야
        resolver.setPrefix("classpath:/templates/"); // classpath:/ => 기본적인 경로
        resolver.setSuffix(".html"); // .mustache 를 .html로 변경

        registry.viewResolver(resolver);
    }
}