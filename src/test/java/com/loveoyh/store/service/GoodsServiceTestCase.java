package com.loveoyh.store.service;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.Order;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceTestCase{
	@Resource
	public OrderService service;
	
	@Test
	public void testCreate() {
		Integer aid = 31;
		Integer[] cids = {1,2,3};
		Integer uid = 1;
		String username = "root";
		Order order = service.create(aid, cids, uid, username);
		System.err.println("Order:"+order);
		System.err.println("OK.");
	}
}
