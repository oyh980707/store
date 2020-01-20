package com.loveoyh.store.mapper;

import org.springframework.stereotype.Service;

import com.loveoyh.store.entity.Order;
import com.loveoyh.store.entity.OrderItem;

/**
 * 处理订单数据接口
 * @author oyh
 *
 */
public interface OrderMapper {
	/**
	 * 插入订单数据
	 * @param order 订单数据对象
	 * @return 受影响的行数
	 */
	Integer insertOrder(Order order);
	
	/**
	 * 插入订单商品数据
	 * @param orderItem 订单商品数据对象
	 * @return 受影响的行数
	 */
	Integer insertOrderItem(OrderItem orderItem);
}
