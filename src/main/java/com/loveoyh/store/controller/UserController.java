package com.loveoyh.store.controller;

import com.loveoyh.store.controller.ex.*;
import com.loveoyh.store.entity.User;
import com.loveoyh.store.service.UserService;
import com.loveoyh.store.util.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UserController extends BaseController{
	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	/** 上传允许的头像类型 */
	public static final List<String> AVATAR_CONTENT_TYPE = new ArrayList<String>();
	/** 上传头像的最大大小 */
	public static final long AVATAR_MAX_SIZE = 2 * 1024 * 1024;
	/** 上传头像的目录位置 */
	public static final String AVATAR_DIR = "upload";
	
	static {
		AVATAR_CONTENT_TYPE.add("image/jpeg");
		AVATAR_CONTENT_TYPE.add("image/png");
		AVATAR_CONTENT_TYPE.add("image/gif");
	}
	
	@Autowired
	UserService userService;
	
	@RequestMapping("reg")
	public JsonResult reg(User user){
		userService.reg(user);
		return JsonResult.newInstance();
	}
	
	@RequestMapping("login")
	public JsonResult login(String username,String password,HttpSession session){
		User user = userService.login(username, password);
		
		session.setAttribute("uid", user.getUid());
		session.setAttribute("username", user.getUsername());
		
		return JsonResult.newInstance(user);
	}
	
	@GetMapping("get_info")
	public JsonResult getInfo(HttpSession session){
		Integer uid = getUidFromSession(session);
		User user = userService.getByUid(uid);
		return JsonResult.newInstance(user);
	}
	
	@RequestMapping("change_password")
	public JsonResult changePassword(@RequestParam("old_password") String oldPassword,@RequestParam("new_password") String newPassword,HttpSession session){
		//从session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		//执行修改
		userService.changePassword(uid, username, oldPassword, newPassword);
		
		return JsonResult.newInstance();
	}
	
	@RequestMapping("change_info")
	public JsonResult changeInfo(User user,HttpSession session){
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		//设置为修改数据的用户名和id
		user.setUid(uid);
		user.setUsername(username);
		
		userService.changeInfo(user);

		return JsonResult.newInstance();
	}
	
	@PostMapping("change_avatar")
	public JsonResult changeAvatar(HttpServletRequest request,@RequestParam("file") MultipartFile file) {
		//检查文件是否为空
		if(file.isEmpty()) {
			throw new FileEmptyException("文件为空!");
		}
		
		//检查文件大小
		if(file.getSize() > AVATAR_MAX_SIZE) {
			throw new FileSizeException("文件过大!，不能超过"+(AVATAR_MAX_SIZE/1024)+"KB");
		}
		
		//检查文件类型
		if(!AVATAR_CONTENT_TYPE.contains(file.getContentType())) {
			throw new FileTypeException("请使用以下图片类型："+AVATAR_CONTENT_TYPE);
		}
		
		
		//确定文件夹
		String dirPath = request.getServletContext().getRealPath(AVATAR_DIR);
		File dir = new File(dirPath);
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
		//执行保存
		File dest = new File(dir,filename);
		logger.debug("上传路径[{}]文件[{}]",dir,dest.getName());
		try {
			file.transferTo(dest);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			throw new FileUploadStateException("文件传输错误!(状态)");
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileUploadIOException("文件传输中断!");
		}
		//更新数据表
		String avatar = "/"+ AVATAR_DIR +"/"+filename;
		HttpSession session = request.getSession();
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		userService.changeAvatar(uid, username, avatar);
		
		//返回图片地址
		return JsonResult.newInstance(avatar);
	}
}
