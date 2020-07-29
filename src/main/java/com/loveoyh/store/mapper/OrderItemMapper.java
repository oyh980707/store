package com.loveoyh.store.mapper;

import com.loveoyh.store.entity.OrderItem;

import java.util.List;

/**
 * 处理订单与商品对应的数据接口
 * @Created by oyh.Jerry to 2020/07/29 13:45
 */
public interface OrderItemMapper {
	
	/**
	 * 根据订单id查询订单商品
	 * @param oid
	 * @return
	 */
	List<OrderItem> queryByOid(Integer oid);

}
