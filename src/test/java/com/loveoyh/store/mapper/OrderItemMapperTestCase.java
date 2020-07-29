package com.loveoyh.store.mapper;


import com.loveoyh.store.entity.OrderItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderItemMapperTestCase {
	@Resource
	private OrderItemMapper mapper;
	
	@Test
	public void testQueryByUid() {
		List<OrderItem> orderItems = this.mapper.queryByOid(5);
		orderItems.stream().forEach(System.out::println);
		System.err.println("OK.");
	}
	
}