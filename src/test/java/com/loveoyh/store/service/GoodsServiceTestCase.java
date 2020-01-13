package com.loveoyh.store.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.District;
import com.loveoyh.store.entity.Goods;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceTestCase{
	@Resource
	public GoodsService service;
	
	@Test
	public void testGetHotList() {
		List<Goods> list = service.getHotList();
		System.err.println("BEGIN:");
		if(list!=null) {
			for (Goods goods : list) {
				System.err.println(goods);
			}
		}
		System.err.println("END.");
	}
	
	@Test
	public void testGetById() {
		Long id = 10000001L;
		Goods goods = service.getById(id);
		System.err.println("goods:"+goods);
		System.err.println("END.");
	}
	
}
