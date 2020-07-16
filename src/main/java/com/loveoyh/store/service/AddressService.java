package com.loveoyh.store.service;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.service.ex.*;

import java.util.List;

/**
 * 处理地址相关的业务层接口
 * @author oyh
 *
 */
public interface AddressService {
	/**
	 * 地址的数量最大值
	 */
	static int ADDRESS_MAX_COUNT = 20;
	
	/**
	 * 为用户增加一条地址信息
	 * @param address 地址数据对象
	 * @param uid 用户id
	 * @param username 用户名
	 * @throws AddressCountLimitException
	 * @throws InsertException
	 */
	void addnew(Address address, Integer uid, String username) throws AddressCountLimitException, InsertException;
	
	/**
	 * 获取所有用户的收货地址信息
	 * @param uid 用户id
	 * @return 所有用户的收货地址
	 */
	List<Address> getByUid(Integer uid);
	
	/**
	 * 设置默认收货地址
	 * @param aid 收货地址id
	 * @param uid 用户id
	 * @param username 用户名
	 * @throws AddressNotFoundException
	 * @throws AccessDeniedException
	 * @throws UpdateException
	 */
	void setDefault(Integer aid, Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, UpdateException;
	
	/**
	 * 删除收货地址
	 * @param aid 收货地址id
	 * @param uid 用户id
	 * @param username 用户名
	 * @throws AddressNotFoundException
	 * @throws AccessDeniedException
	 * @throws DeleteException
	 */
	void delete(Integer aid,Integer uid,String username) throws AddressNotFoundException,AccessDeniedException,DeleteException;
	
	/**
	 * 根据收货地址的数据id查询详情
	 * @param aid 收货地址的数据id
	 * @return 匹配的收货地址的详情，如果没有匹配的数据，则返回null
	 */
	Address getByAid(Integer aid);
}
