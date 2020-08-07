package com.loveoyh.store.mapper;

import com.loveoyh.store.entity.Order;
import com.loveoyh.store.entity.OrderItem;

import java.util.List;

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
	
	/**
	 * 根据用户id查询所有订单
	 * @param uid 用户id
	 * @return
	 */
	List<Order> queryByUid(Integer uid);
	
	/**
	 * 通过id查询订单
	 * @param id
	 * @return
	 */
	Order queryById(Long id);
}
