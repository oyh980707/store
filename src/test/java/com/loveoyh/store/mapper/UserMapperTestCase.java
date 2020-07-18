package com.loveoyh.store.mapper;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import com.loveoyh.store.entity.User;
import com.loveoyh.store.mapper.UserMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTestCase {
	@Resource
	private UserMapper mapper;	
	
	/**
	 * 测试插入数据
	 */
	@Test
	public void testInsert() {
		User user = new User();
		user.setUsername("oyh");
		user.setPassword("980707");
		Integer rows = mapper.insert(user);
		System.err.println("rows=" + rows);
	}
	
	/**
	 * 测试更新用户密码
	 */
	@Test
	public void testUpdatePassword() {
		Integer uid = 1;
		String password = "980707";
		String salt = mapper.findByUsername("root").getSalt();
		password = password+salt;
		for(int i=0;i<3;i++) {
			password = DigestUtils.md5DigestAsHex(password.getBytes()).toUpperCase();
		}
		String modifiedUser = "root";
		Date modifiedTime = new Date();
		Integer n = mapper.updatePassword(uid, password, modifiedUser, modifiedTime);
		System.err.println(n);
	}
	
	/**
	 * 测试更新用户信息
	 */
	@Test
	public void testUpdateInfo() {
		User user = new User();
		user.setUid(1);
		user.setPhone("18812345678");
		user.setEmail("root@gmail.com");
		user.setGender(0);
		user.setModifiedUser("root");
		user.setModifiedTime(new Date());
		Integer n = mapper.updateInfo(user);
		System.err.println(n);
	}
	
	/**
	 * 测试更新用户头像
	 */
	@Test
	public void testUpdateAvatar() {
		Integer uid = 1;
		String avatar = "/usr/local/java/user.jpg";
		String modifiedUser = "root";
		Date modifiedTime = new Date();
		Integer n = mapper.updateAvatar(uid, avatar, modifiedUser, modifiedTime);
		System.err.println(n);
	}

	/**
	 * 测试通过用户名查找用户
	 */
	@Test
	public void testFindByUsername() {
		String username = "root";
		User user = mapper.findByUsername(username);
		System.err.println(user);
	}
	
	/**
	 * 测试通过用户id(uid)查找用户
	 */
	@Test
	public void testFindByUid() {
		Integer uid = 1;
		User user = mapper.findByUid(uid);
		System.err.println(user);
	}
}
