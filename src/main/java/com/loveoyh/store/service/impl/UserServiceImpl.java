package com.loveoyh.store.service.impl;

import com.loveoyh.store.entity.User;
import com.loveoyh.store.mapper.UserMapper;
import com.loveoyh.store.service.UserService;
import com.loveoyh.store.service.ex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService { 
	
	@Autowired
	private UserMapper userMapper;

	/**
	 * 注册业务流程：
	 * 1.根据参数user对象中的username属性查询数据：userMapper.findByUsername()
	 * 2.判断查询结果是否不为null（查询结果是存在的）。
	 * 		如果是：用户名已被占用，抛出UsernameDuplicateException
	 * 3.基于MD5算法进行密码加密处理
	 * 4.封装用户数据
	 * 5.执行注册：userMapper.insert(user)
	 * 		执行失败抛出InsertException
	 */
	@Override
	public void reg(User user) throws UsernameDuplicateException, InsertException {
		String username = user.getUsername();
		User result = userMapper.findByUsername(username);
		
		if (result != null) {
			throw new UsernameDuplicateException("注册失败！ " + username + "已经被占用！");
		}

		// 得到盐值
		String salt = UUID.randomUUID().toString().toUpperCase();
		// 基于参数user对象中的password进行加密，得到加密后的密码
		String md5Password = getMd5Password(user.getPassword(), salt);
		
		// 将加密后的密码和盐值封装到user中
		user.setSalt(salt);
		user.setPassword(md5Password);
		// 将user中的isDelete设置为0
		user.setIsDelete(0);
		// 封装user中的4个日志属性
		Date now = new Date();
		user.setCreatedUser(username);
		user.setCreatedTime(now);
		user.setModifiedUser(username);
		user.setModifiedTime(now);

		Integer rows = userMapper.insert(user);
		if (rows != 1) {
			throw new InsertException("注册失败！写入数据时出现未知错误！请联系系统管理员！");
		}
	}
	
	/**
	 * 登录业务流程：
	 * 1.根据参数的username属性查询数据：userMapper.findByUsername()
	 * 2.判断查询结果是null,或者是否被删除(1为删除),或者用户名是否与查出的用户名大小写不一致
	 * 3.密码匹配
	 * 4.结果处理并返回
	 */
	@Override
	public User login(String username, String password) throws UserNotFoundException, PasswordNotMatchException {
		User result = userMapper.findByUsername(username);
		
		// 这里的判断顺序不能颠倒
		if (result == null || result.getIsDelete()==1 || !result.getUsername().equals(username)) {
			// 是：用户名不存在，抛出UserNotFoundException
			throw new UserNotFoundException("登录失败！ " + username + "不存在！");
		}
		
		// 得到盐值
		String salt = result.getSalt();
		// 基于参数中的password进行加密，得到加密后的密码
		String md5Password = getMd5Password(password, salt);
		if(!md5Password.equals(result.getPassword())) {
			throw new PasswordNotMatchException("用户密码错误！");
		}

		//处理结果
		result.setPassword(null);
		result.setSalt(null);
		result.setIsDelete(null);
		return result;
	}
	
	/**
	 * 更新密码业务流程：
	 * 1.根据参数uid查询用户数据，并判断查询结果是否为null
	 * 		如果为null，抛出：UserNotFoundException
	 * 2.判断查询结果中的isDelete是否为1
	 * 		如果为1，表示该用户冻结了，抛出：UserNotFoundException
	 * 3.从查询结果中获取盐值，根据参数oldPassword和盐值一起进行加密，得到加密后的密码，
	 * 判断查询结果中的password和以上加密后的密码是否不一致
	 * 		如果不一致就抛出：PasswordNotMatchException
	 * 4.根据参数newPassword和盐值一起进行加密，得到加密后的密码，创建当前时间对象
	 * 执行更新密码，并获取返回的受影响的行数，并判断受影响的行数是否不为1
	 * 		如果不为1则抛出：UpdateException
	 */
	@Override
	public void changePassword(Integer uid, String username, String oldPassword, String newPassword)
			throws UserNotFoundException, PasswordNotMatchException, UpdateException {
		User user = userMapper.findByUid(uid);
		if(user == null) {
			throw new UserNotFoundException("用户不存在!");
		}
		
		if(user.getIsDelete() == 1) {
			throw new UserNotFoundException("用户已经冻结!");
		}
		
		String salt = user.getSalt();
		String oldMd5Password = getMd5Password(oldPassword, salt);
		if(!oldMd5Password.equals(user.getPassword())) {
			throw new PasswordNotMatchException("用户密码错误，无法进行修改!");
		}
		
		String newMd5Password = getMd5Password(newPassword, salt);
		Date modifiedTime = new Date();
		int rows = userMapper.updatePassword(uid, newMd5Password, username, modifiedTime);
		if(rows != 1) {
			throw new UpdateException("更新失败!");
		}
	}
	
	/**
	 * 获取用户数据流程：
	 * 1.根据uid查询用户数据
	 * 2.如果查询到数据，则需要将查询结果中的password、salt、is_delete设置为null
	 * 3.将查询结果返回
	 */
	@Override
	public User getByUid(Integer uid) {
		User result = userMapper.findByUid(uid);
		
		if(result != null) {
			result.setPassword(null);
			result.setSalt(null);
			result.setIsDelete(null);
		}
		
		return result;
	}
	
	/**
	 * 更新用户信息流程
	 * 1.根据参数user中的uid，即user.getUid()查询数据
	 * 2.检查查询结果是否存在，是否标记为删除
	 * 3.封装用户数据
	 * 4.执行修改更新，判断以上修改时的返回值是否不为1
	 * 	如果是则抛出：UpdateException 
	 */
	@Override
	public void changeInfo(User user) throws UserNotFoundException, UpdateException {
		User result = userMapper.findByUid(user.getUid());
		
		if(result == null || result.getIsDelete() == 1) {
			throw new UserNotFoundException("用户不存在");
		}
		
		user.setModifiedUser(user.getUsername());
		user.setModifiedTime(new Date());
		
		int rows = userMapper.updateInfo(user);
		if(rows != 1) {
			throw new UpdateException("更新数据失败！");
		}
	}
	
	/**
	 * 更新用户头像流程
	 * 1.根据参数uid查询用户数据并判断查询结果是否为null
	 * 	如果为null则抛出：UserNotFoundException
	 * 2.判断查询结果中的isDelete为1
	 * 	如果是1则抛出：UserNotFoundException
	 * 3.创建当前时间对象
	 * 4.执行更新头像，并获取返回的受影响的行数并判断受影响的行数是否不为1
	 * 	若为1则抛出：UpdateException
	 */
	@Override
	public void changeAvatar(Integer uid, String username, String avatar) throws UserNotFoundException, UpdateException {
		User user = userMapper.findByUid(uid);
		if(user == null) {
			throw new UserNotFoundException("用户不存在!");
		}
		
		if(user.getIsDelete() == 1) {
			throw new UserNotFoundException("用户已经冻结!");
		}
		
		Date modifiedTime = new Date();
		int rows = userMapper.updateAvatar(uid, avatar, username, modifiedTime);
		if(rows != 1) {
			throw new UpdateException("更新失败!");
		}
		
	}
	
	/**
	 * 对密码进行加密
	 * @param password 原始密码
	 * @param salt 盐值
	 * @return 加密后的密码
	 */
	private String getMd5Password(String password, String salt) {
		//规则：对password+salt进行三次加密
		String str = password+salt;
		for(int i=0;i<3;i++) {
			str = DigestUtils.md5DigestAsHex(str.getBytes()).toUpperCase();
		}
		return str;
	}

}
