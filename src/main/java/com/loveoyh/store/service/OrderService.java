package com.loveoyh.store.service;

import com.loveoyh.store.entity.Order;
import com.loveoyh.store.service.ex.InsertException;

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
}
