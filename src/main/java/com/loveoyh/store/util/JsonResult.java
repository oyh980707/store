package com.loveoyh.store.util;

/**
 * 向客户端响应操作结果的数据类型
 * 	
 * 注：
 * 	通过JsonInclude注解去除null的值字段进行数据传输
 */
//@JsonInclude(Include.NON_NULL)
public class JsonResult {
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
	private Object data;

	public JsonResult() {
	}
	
	public JsonResult(Object data) {
		state = SUCCESS;
		this.data = data;
	}
	
	public static JsonResult newInstance(){
		JsonResult jr = new JsonResult();
		jr.setState(SUCCESS);
		return jr;
	}
	
	public static JsonResult newInstance(Object data){
		JsonResult jr = new JsonResult();
		jr.setState(SUCCESS);
		jr.setData(data);
		return jr;
	}
	
	public static JsonResult newInstance(int state, String message){
		JsonResult jr = new JsonResult();
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


	public Object getData() {
		return data;
	}


	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		if(null == data){
			return "JsonResult{" +
					"state=" + state +
					", message='" + message + "}";
		}
		return "JsonResult{" +
				"state=" + state +
				", message='" + message + '\'' +
				", data=" + data!=null?data.toString():"" +
				'}';
	}
}
