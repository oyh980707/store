package com.loveoyh.store.controller.ex;
/**
 * 文件为空异常类，例如没有选择文件或选择的文件为0字节的
 * @author HP
 *
 */
public class FileEmptyException extends FileUploadException {
	private static final long serialVersionUID = 1L;

	public FileEmptyException() {
	}

	public FileEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileEmptyException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileEmptyException(String message) {
		super(message);
	}

	public FileEmptyException(Throwable cause) {
		super(cause);
	}

}
