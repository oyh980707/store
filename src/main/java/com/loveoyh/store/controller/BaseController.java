package com.loveoyh.store.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ExceptionHandler;

import com.loveoyh.store.controller.ex.ControllerException;
import com.loveoyh.store.controller.ex.FileEmptyException;
import com.loveoyh.store.controller.ex.FileSizeException;
import com.loveoyh.store.controller.ex.FileTypeException;
import com.loveoyh.store.controller.ex.FileUploadException;
import com.loveoyh.store.controller.ex.FileUploadIOException;
import com.loveoyh.store.controller.ex.FileUploadStateException;
import com.loveoyh.store.service.ex.AccessDeniedException;
import com.loveoyh.store.service.ex.AddressCountLimitException;
import com.loveoyh.store.service.ex.CartNotFoundException;
import com.loveoyh.store.service.ex.DeleteException;
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
	
	@ExceptionHandler({ServiceException.class,ControllerException.class})
	public JsonResult<Void> handlerException(Throwable e){
		JsonResult<Void> jr = new JsonResult<Void>();
		jr.setState(JsonResult.ERROR);
		if(e instanceof UsernameDuplicateException) {
			//4001-用户名冲突异常类
			jr.setState(4001);
		}else if(e instanceof UserNotFoundException){
			//4002-用户不存在
			jr.setState(4002);
		}else if(e instanceof PasswordNotMatchException){
			//4003-密码不匹配
			jr.setState(4003);
		}else if(e instanceof CartNotFoundException){
			//4004-购物车不匹配
			jr.setState(4004);
		}else if(e instanceof AddressCountLimitException){
			//4004-地址数量范围异常类
			jr.setState(4004);
		}else if(e instanceof AccessDeniedException){
			//4005-访问拒绝
			jr.setState(4005);
		}else if(e instanceof InsertException){
			//5000-插入数据异常
			jr.setState(5000);
		}else if(e instanceof UpdateException){
			//5001-数据更新异常
			jr.setState(5001);
		}else if(e instanceof DeleteException){
			//5002-删除数据异常类
			jr.setState(5002);
		}else if(e instanceof FileEmptyException) {
			//6000-文件为空异常类，例如没有选择文件或选择的文件为0字节的
			jr.setState(6000);
		}else if(e instanceof FileSizeException) {
			//6001-文件过大异常类，例如上传的文件大小超过规定的大小
			jr.setState(6001);
		}else if(e instanceof FileTypeException) {
			//6002-文件类型错误异常类，例如文件类型超出规定类型
			jr.setState(6002);
		}else if(e instanceof FileUploadIOException) {
			//6003-文件上传IO异常，例如：上传文件的读写问题
			jr.setState(6003);
		}else if(e instanceof FileUploadStateException) {
			//6004-文件上传状态异常类，例如：上传过程中源文件被移除，导致源文件找不到
			jr.setState(6004);
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
