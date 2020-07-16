package com.loveoyh.store.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 向客户端响应操作结果的数据类型
 * 	
 * 注：
 * 	通过JsonInclude注解去除null的值字段进行数据传输
 * @param <T> 向客户端响应的数据的类型
 */
//@JsonInclude(Include.NON_NULL)
public class JsonResult<T> {
	/** 
	 * 表示成功状态标志 
	 */
	public static final int SUCCESS = 0;
	/** 
	 * 表示出错状态标志 
	 */
	public static final int ERROR = 1;
	
	/**
	 * 状态
	 */
	private int state;
	/**
	 * 错误消息
	 */
	private String message;
	/**
	 * 返回正确的时候的数据
	 */
	private T data;

	public JsonResult() {
	}
	
	public JsonResult(T data) {
		state = SUCCESS;
		this.data = data;
	}
	
	public JsonResult<T> newInstance(){
		JsonResult<T> jr = new JsonResult<>();
		jr.setState(SUCCESS);
		return jr;
	}
	
	public JsonResult<T> newInstance(T data){
		JsonResult<T> jr = new JsonResult<>();
		jr.setState(SUCCESS);
		jr.setData(data);
		return jr;
	}
	
	
	public JsonResult<T> newInstance(int state, String message){
		JsonResult<T> jr = new JsonResult<>();
		jr.setState(state);
		jr.setMessage(message);
		return jr;
	}
	
	public Integer getState() {
		return state;
	}


	public void setState(Integer state) {
		this.state = state;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public T getData() {
		return data;
	}


	public void setData(T data) {
		this.data = data;
	}
	
}
