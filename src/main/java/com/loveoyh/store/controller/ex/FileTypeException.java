package com.loveoyh.store.controller.ex;
/**
 * 文件类型错误异常类，例如文件类型超出规定类型
 * @author HP
 *
 */
public class FileTypeException extends FileUploadException {
	private static final long serialVersionUID = 1L;

	public FileTypeException() {
	}

	public FileTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileTypeException(String message) {
		super(message);
	}

	public FileTypeException(Throwable cause) {
		super(cause);
	}

}
