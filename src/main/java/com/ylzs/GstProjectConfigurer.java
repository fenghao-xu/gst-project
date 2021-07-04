package com.ylzs;

import com.ylzs.controller.auth.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * 说明：
 * @author Administrator
 * 2019-10-16 14:28
 */
@Configuration
public class GstProjectConfigurer implements WebMvcConfigurer { //extends WebMvcConfigurerAdapter

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor()).addPathPatterns("/**");
        //super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/upload/**")
//                .addResourceLocations("classpath:/upload/");
        registry.addResourceHandler("/**/index.html", "/")//设置哪些静态资源不缓存
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noStore());//用 noStore 才起效
    }


}
