package com.loveoyh.store.service;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.District;
import com.loveoyh.store.service.ex.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartServiceTestCase{
	@Resource
	public CartService service;
	
	@Test
	public void testGetByParent() {
		try {
			Cart cart = new Cart();
			cart.setGid(10000001l);
			cart.setNum(1);
			Integer uid = 1;
			String username = "root";
			service.addToCart(cart, uid, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
}
