package com.loveoyh.store.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.User;
import com.loveoyh.store.service.ex.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTestCase {
	@Autowired
	private UserService userService;	
	
	/**
	 * 测试注册
	 */
	@Test
	public void testReg() {
		try {
			User user = new User();
			user.setUsername("oyh");
			user.setPassword("980707");
			userService.reg(user);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * 测试登录
	 */
	@Test
	public void testLogin() {
		try {
			String username = "root";
			String password = "1234";
			User user = userService.login(username, password);
			System.err.println("登录成功用户："+user.getUsername());
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * 测试更新密码
	 */
	@Test
	public void testChangePassword() {
		try {
			Integer uid = 1;
			String username = "root";
			String oldPassword = "123456";
			String newPassword = "980707";
			userService.changePassword(uid, username, oldPassword, newPassword);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * 测试获取用户数据根据uid
	 */
	@Test
	public void testGetByUid() {
		Integer uid = 1;
		User user = userService.getByUid(uid);
		System.err.println(user);
		System.err.println("testGetByUid: OK.");
	}
	
	/**
	 * 测试更新用户信息
	 */
	@Test
	public void testChangeInfo() {
		try {
			User user = new User();
			user.setUid(1);
			user.setUsername("root");
			user.setPhone("13188888888");
			user.setEmail("root@gmail.com");
			userService.changeInfo(user);
			System.err.println("testChangeInfo() is OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
}
