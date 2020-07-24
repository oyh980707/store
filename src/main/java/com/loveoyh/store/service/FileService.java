package com.loveoyh.store.service;

import com.jcraft.jsch.SftpException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件管理
 * @Created by oyh.Jerry to 2020/07/24 14:31
 */
public interface FileService {
	
	/**
	 * 上传图片，返回文件地址
	 * @param file
	 * @return
	 */
	String uploadImage(MultipartFile file) throws IOException, SftpException;
	
}
