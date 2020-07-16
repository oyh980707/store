package com.loveoyh.store.conf;

import com.loveoyh.store.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录拦截器的配置类
 */
@Configuration
public class LoginInterceptorConfigurer implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 创建拦截器对象
		HandlerInterceptor interceptor = new LoginInterceptor();
		
		List<String> excludePaths = new ArrayList<String>();
		excludePaths.add("/web/login.html");
		excludePaths.add("/web/register.html");
		excludePaths.add("/bootstrap3/**");
		excludePaths.add("/css/**");
		excludePaths.add("/images/**");
		excludePaths.add("/js/**");
		excludePaths.add("/web/index.html");
		excludePaths.add("/web/product.html");
		
		excludePaths.add("/users/login");
		excludePaths.add("/users/reg");
		excludePaths.add("/districts/");
		excludePaths.add("/goods/**");
		
		registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(excludePaths);
	}
	
}
