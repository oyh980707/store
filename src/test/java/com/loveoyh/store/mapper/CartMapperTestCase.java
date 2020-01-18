package com.loveoyh.store.mapper;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.District;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartMapperTestCase {
	@Resource
	private CartMapper mapper;
	
	@Test
	public void testInsert() {
		Cart cart = new Cart();
		cart.setUid(1);
		cart.setGid(10000001l);
		Date time = new Date();
		cart.setCreatedTime(time);
		cart.setCreatedUser("root");
		cart.setNum(1);
		cart.setModifiedTime(time);
		cart.setModifiedUser("root");
		int rows = mapper.insert(cart);
		System.err.println("rows:"+rows);
		System.err.println("END.");
	}
	
	@Test
	public void testUpdateNum() {
		int rows = mapper.updateNum(1, 2, "root", new Date());
		System.err.println("rows:"+rows);
		System.err.println("OK.");
	}
	
	@Test
	public void testFindByUidAndGid() {
		Cart cart = mapper.findByUidAndGid(1, 10000001l);
		System.err.println("cart:"+cart);
		System.err.println("OK.");
	}
	
}