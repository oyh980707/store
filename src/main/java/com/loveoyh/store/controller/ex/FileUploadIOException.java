package com.loveoyh.store.controller.ex;
/**
 * 文件上传IO异常，例如：上传文件的读写问题
 * @author HP
 *
 */
public class FileUploadIOException extends FileUploadException {
	private static final long serialVersionUID = 1L;

	public FileUploadIOException() {
	}

	public FileUploadIOException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileUploadIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileUploadIOException(String message) {
		super(message);
	}

	public FileUploadIOException(Throwable cause) {
		super(cause);
	}

}
