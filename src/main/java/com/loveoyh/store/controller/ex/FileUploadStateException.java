package com.loveoyh.store.controller.ex;
/**
 * 文件上传状态异常类，例如：上传过程中源文件被移除，导致源文件找不到
 * @author HP
 *
 */
public class FileUploadStateException extends FileUploadException {
	private static final long serialVersionUID = 1L;

	public FileUploadStateException() {
	}

	public FileUploadStateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileUploadStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileUploadStateException(String message) {
		super(message);
	}

	public FileUploadStateException(Throwable cause) {
		super(cause);
	}

}
