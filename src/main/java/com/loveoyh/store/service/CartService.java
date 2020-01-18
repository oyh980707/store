package com.loveoyh.store.service;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.service.ex.InsertException;
import com.loveoyh.store.service.ex.UpdateException;

/**
 * 处理购物车数据接口
 * @author oyh
 *
 */
public interface CartService {
	/**
	 * 添加商品到购物车中
	 * @param cart 购物车数据
	 * @param uid 用户id
	 * @param username 用户名
	 * @throws InsertException
	 * @throws UpdateException
	 */
	public void addToCart(Cart cart, Integer uid, String username) throws InsertException, UpdateException;
	
	
}
