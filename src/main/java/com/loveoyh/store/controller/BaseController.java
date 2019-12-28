package com.loveoyh.store.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ExceptionHandler;

import com.loveoyh.store.service.ex.InsertException;
import com.loveoyh.store.service.ex.PasswordNotMatchException;
import com.loveoyh.store.service.ex.ServiceException;
import com.loveoyh.store.service.ex.UpdateException;
import com.loveoyh.store.service.ex.UserNotFoundException;
import com.loveoyh.store.service.ex.UsernameDuplicateException;
import com.loveoyh.store.util.JsonResult;

/**
 * 基类控制器
 */
public abstract class BaseController {
	/**
	 * 成功状态
	 */
	protected static final Integer SUCCESS = 0;
	
	@ExceptionHandler(ServiceException.class)
	public JsonResult<Void> handlerException(Throwable e){
		JsonResult<Void> jr = new JsonResult<Void>(e.getMessage());
		if(e instanceof UsernameDuplicateException) {
			//4001-用户名冲突异常类
			jr.setState(4001);
		}else if(e instanceof UserNotFoundException){
			//4002-用户不存在
			jr.setState(4002);
		}else if(e instanceof PasswordNotMatchException){
			//4003-密码不匹配
			jr.setState(4003);
		}else if(e instanceof InsertException){
			//5000-插入数据异常
			jr.setState(5000);
		}else if(e instanceof UpdateException){
			//5001-数据更新异常
			jr.setState(5001);
		}
		return jr;
	}
	
	/**
	 * 从Session中获取当前登录的用户的id
	 * @param session
	 * @return 当前登录的用户的id
	 */
	protected final Integer getUidFromSession(HttpSession session) {
		return Integer.valueOf(session.getAttribute("uid").toString());
	}

	/**
	 * 从Session中获取当前登录的用户名
	 * @param session
	 * @return 当前登录的用户名
	 */
	protected final String getUsernameFromSession(HttpSession session) {
		return session.getAttribute("username").toString();
	}
}
