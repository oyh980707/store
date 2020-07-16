package com.loveoyh.store.service;

import com.loveoyh.store.entity.User;
import com.loveoyh.store.service.ex.*;

/**
 * 处理用户数据的业务层接口
 * @author oyh
 *
 */
public interface UserService {
	/**
	 * 用户注册
	 * @param user 用户数据对象
	 * @throws UsernameDuplicateException
	 * @throws InsertException
	 */
	void reg(User user) throws UsernameDuplicateException,InsertException;
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 * @throws UserNotFoundException
	 * @throws PasswordNotMatchException
	 */
	User login(String username,String password) throws UserNotFoundException,PasswordNotMatchException;
	
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
	void changePassword(Integer uid, String username, String oldPassword, String newPassword) throws UserNotFoundException, PasswordNotMatchException, UpdateException;
	
	/**
	 * 获取用户数据
	 * @param uid 用户id
	 * @return
	 */
	User getByUid(Integer uid);
	
	/**
	 * 更新用户信息
	 * @param user 用户数据对象
	 * @throws UserNotFoundException
	 * @throws UpdateException
	 */
	void changeInfo(User user) throws UserNotFoundException,UpdateException;
	
	/**
	 * 修改头像
	 * @param uid 用户的id
	 * @param username 用户名
	 * @param avatar 头像的路径
	 * @throws UserNotFoundException 用户数据不存在，或者已经被标记为删除
	 * @throws UpdateException 更新数据失败
	 */
	void changeAvatar(Integer uid, String username, String avatar) throws UserNotFoundException, UpdateException;
	
}
