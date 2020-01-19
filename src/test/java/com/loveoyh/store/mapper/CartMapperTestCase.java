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
import com.loveoyh.store.entity.vo.CartVO;

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
	
	@Test
	public void testFindByUid() {
		Integer uid = 1;
		List<CartVO> lists = mapper.findByUid(uid);
		System.err.println("BEGIN:");
		for (CartVO item : lists) {
			System.err.println(item);
		}
		System.err.println("END.");
	}
	
	@Test
	public void testFindByCid() {
		Integer cid = 2;
		Cart cart = mapper.findByCid(cid);
		System.err.println("cart:"+cart);
		System.err.println("END.");
	}
	
	@Test
	public void testFindByCids() {
		Integer[] cids = {1,2};
		List<CartVO> lists = mapper.findByCids(cids);
		System.err.println("BEGIN:");
		for (CartVO item : lists) {
			System.err.println(item);
		}
		System.err.println("END.");
	}
}