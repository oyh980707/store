package com.loveoyh.store.controller.ex;
/**
 * 文件过大异常类，例如上传的文件大小超过规定的大小
 * @author HP
 *
 */
public class FileSizeException extends FileUploadException {
	private static final long serialVersionUID = 1L;

	public FileSizeException() {
	}

	public FileSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileSizeException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileSizeException(String message) {
		super(message);
	}

	public FileSizeException(Throwable cause) {
		super(cause);
	}

}
