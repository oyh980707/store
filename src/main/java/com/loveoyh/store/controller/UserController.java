package com.loveoyh.store.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loveoyh.store.entity.User;
import com.loveoyh.store.service.UserService;
import com.loveoyh.store.util.JsonResult;

@RestController
@RequestMapping("users")
public class UserController extends BaseController{
	
	@Autowired
	UserService userService;
	
	@RequestMapping("reg")
	public JsonResult<Void> reg(User user){
		userService.reg(user);
		return new JsonResult<Void>(SUCCESS);
	}
	
	@RequestMapping("login")
	public JsonResult<User> login(String username,String password,HttpSession session){
		User user = userService.login(username, password);
		
		session.setAttribute("uid", user.getUid());
		session.setAttribute("username", user.getUsername());
		
		return new JsonResult<User>(user);
	}
	
	@GetMapping("get_info")
	public JsonResult<User> getInfo(HttpSession session){
		Integer uid = getUidFromSession(session);
		User user = userService.getByUid(uid);
		return new JsonResult<User>(user);
	}
	
	@RequestMapping("change_password")
	public JsonResult<Void> changePassword(@RequestParam("old_password") String oldPassword,@RequestParam("new_password") String newPassword,HttpSession session){
		//从session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		//执行修改
		userService.changePassword(uid, username, oldPassword, newPassword);
		return new JsonResult<Void>(SUCCESS);
	}
	
	@RequestMapping("change_info")
	public JsonResult<Void> changeInfo(User user,HttpSession session){
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		//设置为修改数据的用户名和id
		user.setUid(uid);
		user.setUsername(username);
		
		userService.changeInfo(user);
		
		return new JsonResult<Void>(SUCCESS);
	}
}
