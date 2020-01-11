package com.loveoyh.store.service.ex;
/**
 * 地址数量范围异常类
 * @author oyh
 *
 */
public class AddressCountLimitException extends ServiceException{
	private static final long serialVersionUID = 1L;

	public AddressCountLimitException() {
		super();
	}

	public AddressCountLimitException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AddressCountLimitException(String message, Throwable cause) {
		super(message, cause);
	}

	public AddressCountLimitException(String message) {
		super(message);
	}

	public AddressCountLimitException(Throwable cause) {
		super(cause);
	}
	
}
