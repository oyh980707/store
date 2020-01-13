package com.loveoyh.store.mapper;

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
public class GoodsMapperTestCase {
	@Resource
	private GoodsMapper mapper;
	
	@Test
	public void testFindHotList() {
		List<Goods> list = mapper.findHotList();
		System.err.println("BGAIN:");
		for (Goods goods : list) {
			System.err.println(goods);
		}
		System.err.println("END.");
	}
	
	@Test
	public void testFindByid() {
		Long id = 10000001L;
		Goods goods = mapper.findById(id);
		System.err.println("goods:"+goods);
		System.err.println("OK.");
	}
	
}