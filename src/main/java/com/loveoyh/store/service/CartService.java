package com.loveoyh.store.service;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.vo.CartVO;
import com.loveoyh.store.service.ex.AccessDeniedException;
import com.loveoyh.store.service.ex.CartNotFoundException;
import com.loveoyh.store.service.ex.InsertException;
import com.loveoyh.store.service.ex.UpdateException;

import java.util.List;

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
	void addToCart(Cart cart, Integer uid, String username) throws InsertException, UpdateException;
	
	/**
	 * 通过用户id查询有关显示购物车相关的数据
	 * @param uid 用户id
	 * @return 购物车相关数据，通过类CartVO携带数据
	 */
	List<CartVO> getByUid(Integer uid);
	
	/**
	 * 购物车商品增加数量（num += 1）
	 * @param cid 购物车id
	 * @param uid 用户id
	 * @param username 用户名
	 * @return 商品的数量num
	 * @throws CartNotFoundException
	 * @throws AccessDeniedException
	 * @throws UpdateException
	 */
	Integer increase(Integer cid,Integer uid,String username) throws CartNotFoundException,AccessDeniedException,UpdateException;
	
	/**
	 * 购物车商品减少数量（num -= 1）
	 * @param cid 购物车id
	 * @param uid 用户id
	 * @param username 用户名
	 * @return 商品的数量num
	 * @throws CartNotFoundException
	 * @throws AccessDeniedException
	 * @throws UpdateException
	 */
	Integer reduce(Integer cid,Integer uid,String username) throws CartNotFoundException,AccessDeniedException,UpdateException;
	
	/**
	 * 通过多个购物车id查询有关显示购物车相关的数据集合
	 * @param cids 多个购物车数据id
	 * @param uid 用户id
	 * @return匹配购物车数据集合，如果没有匹配的数据则返回null
	 */
	List<CartVO> getByCids(Integer[] cids, Integer uid);
	
	/**
	 * 删除指定的购物车商品
	 * @param cids 多个购物车数据id
	 * @param uid 用户id
	 * @return
	 */
	Integer delete(Integer[] cids, Integer uid);
}
