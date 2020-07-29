package com.loveoyh.store.service.impl;

import com.loveoyh.store.entity.OrderItem;
import com.loveoyh.store.mapper.OrderItemMapper;
import com.loveoyh.store.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 商品和订单的对应 实现类
 * @Created by oyh.Jerry to 2020/07/29 16:19
 */
@Service("orderItemService")
public class OrderItemServiceImpl implements OrderItemService {
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Override
	public List<OrderItem> queryByOid(Integer oid) {
		if(Objects.isNull(oid)){
		
		}
		return this.orderItemMapper.queryByOid(oid);
	}
	
}
