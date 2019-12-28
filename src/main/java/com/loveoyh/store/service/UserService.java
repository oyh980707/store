package com.loveoyh.store.service;

import com.loveoyh.store.entity.User;
import com.loveoyh.store.service.ex.InsertException;
import com.loveoyh.store.service.ex.PasswordNotMatchException;
import com.loveoyh.store.service.ex.UpdateException;
import com.loveoyh.store.service.ex.UserNotFoundException;
import com.loveoyh.store.service.ex.UsernameDuplicateException;

/**
 * 处理用户数据的业务层接口
 * @author HP
 *
 */
public interface UserService {
	/**
	 * 用户注册
	 * @param user 用户数据对象
	 * @throws UsernameDuplicateException
	 * @throws InsertException
	 */
	public void reg(User user) throws UsernameDuplicateException,InsertException;
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 * @throws UserNotFoundException
	 * @throws PasswordNotMatchException
	 */
	public User login(String username,String password) throws UserNotFoundException,PasswordNotMatchException;
	
	/**
	 * 更新用户密码
	 * @param uid 用户id
	 * @param username 用户名
	 * @param oldPassword 旧密码
	 * @param newPassword 新密码
	 * @throws UserNotFoundException
	 * @throws PasswordNotMatchException
	 * @throws UpdateException
	 */
	public void changePassword(Integer uid, String username, String oldPassword, String newPassword) throws UserNotFoundException, PasswordNotMatchException, UpdateException;
	
	/**
	 * 获取用户数据
	 * @param uid 用户id
	 * @return
	 */
	public User getByUid(Integer uid);
	
	/**
	 * 更新用户信息
	 * @param user 用户数据对象
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	public void changeInfo(User user) throws UserNotFoundException,UpdateException;
	
	
	
}
