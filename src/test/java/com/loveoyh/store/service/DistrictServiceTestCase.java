package com.loveoyh.store.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.District;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DistrictServiceTestCase{
	@Resource
	public DistrictService service;
	
	@Test
	public void testGetByParent() {
		String parent = "861";
		List<District> list = service.getByParent(parent);
		System.err.println("BEGIN:");
		if(list!=null) {
			for (District district : list) {
				System.err.println(district);
			}
		}
		System.err.println("END.");
	}
	
	@Test
	public void testGetByCode() {
		String code = "422801";
		District district = service.getByCode(code);
		System.err.println("name:"+district.getName());
		System.err.println("OK.");
	}
}
