package com.loveoyh.store.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

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
	public Integer insert(Address address);
	
	/**
	 * 根据用户id查询用户的地址数量
	 * @param uid 用户id
	 * @return 查询行数
	 */
	public Integer countByUid(Integer uid);
	
	/**
	 * 根据用户id查询所有的地址信息
	 * @param uid 用户id
	 * @return 查询所有的地址
	 */
	public List<Address> findByUid(Integer uid);

	/**
	 * 根据收货地址id查找
	 * @param aid 收货地址id
	 * @return 匹配的地址数据，若没有匹配数据则返回null
	 */
	public Address findByAid(Integer aid);
	
	/**
	 * 获取最后修改的收货地址
	 * @param uid 用户id
	 * @return 匹配的收货地址数据，若没有匹配的则返回null
	 */
	public Address findLastModified(Integer uid);
	
	/**
	 * 根据收货地址id删除收货地址
	 * @param aid 收货地址id
	 * @return 受影响的行数
	 */
	public Integer deleteByAid(Integer aid);
	
	/**
	 * 更新-将默认地址设为非默认
	 * @param uid 用户id
	 * @return 受影响的行数
	 */
	public Integer updateNonDefault(Integer uid);
	
	/**
	 * 更新-将指定地址设为默认
	 * @param aid 收货地址id
	 * @param modifiedUser 操作的用户名
	 * @param modifiedTime 操作时间
	 * @return 受影响的行数
	 */
	public Integer updateDefault(@Param("aid") Integer aid, @Param("modifiedUser") String modifiedUser, @Param("modifiedTime") Date modifiedTime);
	
}
