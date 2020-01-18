package com.loveoyh.store.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.loveoyh.store.entity.Cart;

/**
 * 购物车数据接口
 * @author oyh
 *
 */
public interface CartMapper {
	/**
	 * 插入购物车数据
	 * @param cart 购物车数据
	 * @return 受影响的行数
	 */
	public Integer insert(Cart cart);
	
	/**
	 * 修改购物车中商品的数量
	 * @param cid 购物车id
	 * @param num 商品数量
	 * @param modifiedUser 修改用户
	 * @param modifiedTime 修改时间
	 * @return 受影响的行数
	 */
	public Integer updateNum(@Param("cid") Integer cid, @Param("num") Integer num, @Param("modifiedUser") String modifiedUser, @Param("modifiedTime") Date modifiedTime);
	
	/**
	 * 根据用户id和商品id查询购物车数据
	 * @param uid 用户id
	 * @param gid 商品id
	 * @return 购物车数据，包含cid和num
	 */
	public Cart findByUidAndGid(@Param("uid") Integer uid, @Param("gid") Long gid);
}
