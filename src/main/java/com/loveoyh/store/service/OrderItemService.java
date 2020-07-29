package com.loveoyh.store.service;

import com.loveoyh.store.entity.OrderItem;

import java.util.List;

/**
 * 商品和订单的对应 接口
 * @Created by oyh.Jerry to 2020/07/29 16:18
 */
public interface OrderItemService {

	List<OrderItem> queryByOid(Integer oid);
	
}
