package com.loveoyh.store.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 */
public class LoginInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		/*
		 * 验证用户是否登录，如果已登录，放行，如果未登录，拦截，并重定向到登录页面
		 */
		HttpSession session = request.getSession();
		//判断Session中有没有uid，因为登录成功时往Session中存入了uid，所以可以使用uid作为判断标准
		if(session.getAttribute("uid") == null) {
			// 重定向到登录页面
			response.sendRedirect("/web/login.html");
			// 执行拦截
			return false;
		}
		return true;
	}

}
