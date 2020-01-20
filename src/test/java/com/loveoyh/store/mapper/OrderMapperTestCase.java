package com.loveoyh.store.mapper;


import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.Order;
import com.loveoyh.store.entity.OrderItem;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderMapperTestCase {
	@Resource
	private OrderMapper mapper;
	
	@Test
	public void testInsertOrder() {
		Order order = new Order();
		int rows = mapper.insertOrder(order);
		System.err.println("rows:"+rows);
		System.err.println("OK.");
	}
	
	@Test
	public void testInsertOrderItem() {
		OrderItem order = new OrderItem();
		int rows = mapper.insertOrderItem(order);
		System.err.println("rows:"+rows);
		System.err.println("OK.");
	}
	
}