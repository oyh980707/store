package com.loveoyh.store.service.impl;

import com.jcraft.jsch.SftpException;
import com.loveoyh.store.service.FileService;
import com.loveoyh.store.util.SFTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件管理类
 * @Created by oyh.Jerry to 2020/07/24 14:32
 */
@Service("fileService")
public class FileServiceImpl implements FileService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Value("${upload.sftp-port}")
	private String ftpPort ;
	@Value("${upload.sftp-username}")
	private String ftpUsername ;
	@Value("${upload.sftp-password}")
	private String ftpPassword ;
	@Value("${upload.sftp-server}")
	private String sftpServer;
	/** 上传头像的目录位置 */
	@Value("${upload.base-dir}")
	private String baseDir;
	/** 上传头像的相对位置 */
	@Value("${upload.avatar-dir}")
	private String avatarDir;
	@Value("${upload.image-port}")
	private String imagePort;
	
	@Override
	public String uploadImage(MultipartFile file) throws IOException, SftpException {
		//确定文件夹
		File dir = new File(baseDir + avatarDir);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		//确定文件名
		String originalFilename = file.getOriginalFilename();
		String suffix = "";
		int beginIndex = originalFilename.lastIndexOf(".");
		if(beginIndex != -1) {
			suffix = originalFilename.substring(beginIndex);
		}
		String filename = UUID.randomUUID().toString() + suffix;
		LOGGER.debug("上传路径[{}]文件[{}]",dir,filename);
		SFTPUtil sftp = new SFTPUtil(ftpUsername, ftpPassword, sftpServer, Integer.valueOf(ftpPort));
		sftp.login();
		InputStream is = file.getInputStream();
		
		sftp.upload(baseDir+avatarDir, filename, is);
		sftp.logout();
		return "http://"+sftpServer+":"+imagePort+avatarDir+"/"+filename;
	}
	
}
