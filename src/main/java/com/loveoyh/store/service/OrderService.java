package com.loveoyh.store.service;

import com.loveoyh.store.entity.Order;
import com.loveoyh.store.service.ex.InsertException;

import java.util.List;

/**
 * 处理订单数据接口
 * @author oyh
 *
 */
public interface OrderService {
	
	/**
	 * 创建订单
	 * @param aid 地址id
	 * @param cids 购物车id
	 * @param uid 用户id
	 * @param username 用户名
	 * @return 订单数据对象
	 * @throws InsertException
	 */
	Order create(Integer aid, Integer[] cids, Integer uid, String username) throws InsertException;
	
	/**
	 * 通过用户id查询所有订单
	 * @param uid
	 * @return
	 */
	List<Order> queryByUid(Integer uid);
	
	/**
	 * 转化额外属性
	 * @param orders
	 * @return
	 */
	List<Order> convertOrderList(List<Order> orders);
	
	/**
	 * 转化额外属性
	 * @param order
	 * @return
	 */
	Order convertOrderList(Order order);
	
	/**
	 * 通过id查询订单
	 * @param id
	 * @return
	 */
	Order queryById(Long id);
}
