package com.loveoyh.store.service;

import java.util.List;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.service.ex.AccessDeniedException;
import com.loveoyh.store.service.ex.AddressCountLimitException;
import com.loveoyh.store.service.ex.AddressNotFoundException;
import com.loveoyh.store.service.ex.DeleteException;
import com.loveoyh.store.service.ex.InsertException;
import com.loveoyh.store.service.ex.UpdateException;

/**
 * 处理地址相关的业务层接口
 * @author oyh
 *
 */
public interface AddressService {
	/**
	 * 地址的数量最大值
	 */
	public static int ADDRESS_MAX_COUNT = 20;
	
	/**
	 * 为用户增加一条地址信息
	 * @param address 地址数据对象
	 * @param uid 用户id
	 * @param username 用户名
	 * @throws AddressCountLimitException
	 * @throws InsertException
	 */
	public void addnew(Address address, Integer uid, String username) throws AddressCountLimitException, InsertException;
	
	/**
	 * 获取所有用户的收货地址信息
	 * @param uid 用户id
	 * @return 所有用户的收货地址
	 */
	public List<Address> getByUid(Integer uid);
	
	/**
	 * 设置默认收货地址
	 * @param aid 收货地址id
	 * @param uid 用户id
	 * @param username 用户名
	 * @throws AddressNotFoundException
	 * @throws AccessDeniedException
	 * @throws UpdateException
	 */
	public void setDefault(Integer aid, Integer uid, String username) throws AddressNotFoundException, AccessDeniedException, UpdateException;
	
	/**
	 * 删除收货地址
	 * @param aid 收货地址id
	 * @param uid 用户id
	 * @param username 用户名
	 * @throws AddressNotFoundException
	 * @throws AccessDeniedException
	 * @throws DeleteException
	 */
	public void delete(Integer aid,Integer uid,String username) throws AddressNotFoundException,AccessDeniedException,DeleteException;
}
