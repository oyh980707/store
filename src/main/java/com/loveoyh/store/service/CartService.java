package com.loveoyh.store.service;

import java.util.List;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.vo.CartVO;
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
	
	/**
	 * 通过用户id查询有关显示购物车相关的数据
	 * @param uid 用户id
	 * @return 购物车相关数据，通过类CartVO携带数据
	 */
	public List<CartVO> getByUid(Integer uid);
	
}
