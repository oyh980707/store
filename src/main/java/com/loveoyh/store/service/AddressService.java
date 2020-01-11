package com.loveoyh.store.service;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.service.ex.AddressCountLimitException;
import com.loveoyh.store.service.ex.InsertException;

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
	
}
