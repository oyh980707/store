package com.loveoyh.store.mapper;

import com.loveoyh.store.entity.Address;

/**
 * 处理地址数据接口
 * @author HP
 *
 */
public interface AddressMapper {
	/**
	 * 插入一条地址信息
	 * @param address 地址数据对象
	 * @return 受影响的行数
	 */
	Integer insert(Address address);
	
	/**
	 * 根据用户id查询用户的地址数量
	 * @param uid 用户id
	 * @return 查询行数
	 */
	Integer countByUid(Integer uid);
}
