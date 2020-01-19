package com.loveoyh.store.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.District;
import com.loveoyh.store.entity.vo.CartVO;
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
	
	@Test
	public void testGetByUid() {
		Integer uid = 1;
		List<CartVO> lists = service.getByUid(uid);
		System.err.println("BEGIN:");
		for (CartVO cartVO : lists) {
			System.err.println(cartVO);
		}
		System.err.println("END.");
	}
	
	@Test
	public void testGetByCids() {
		Integer uid = 1;
		Integer[] cids = {1,2,3,4};
		List<CartVO> lists = service.getByCids(cids,uid);
		System.err.println("BEGIN:");
		for (CartVO item : lists) {
			System.err.println(item);
		}
		System.err.println("END.");
	}
	
	@Test
	public void testIncrease() {
		try {
			Integer cid = 1;
			Integer uid = 1;
			String username = "root";
			Integer num = service.increase(cid, uid, username);
			System.err.println("num:"+num);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void testReduce() {
		try {
			Integer cid = 1;
			Integer uid = 1;
			String username = "root";
			Integer num = service.reduce(cid, uid, username);
			System.err.println("num:"+num);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	
}
