package com.loveoyh.store.mapper;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.District;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DistrictMapperTestCase {
	@Resource
	private DistrictMapper mapper;
	
	@Test
	public void testFindByParent() {
		String parent = "86";
		List<District> list = mapper.findByParent(parent);
		System.err.println("BGAIN:");
		for (District district : list) {
			System.err.println(district);
		}
		System.err.println("END.");
	}
	
	@Test
	public void testFindByCode() {
		String code = "422801";
		District district = mapper.findByCode(code);
		System.err.println("district:"+district);
		System.err.println("OK.");
	}
	
}