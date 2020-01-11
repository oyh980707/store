package com.loveoyh.store.mapper;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.Address;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressMapperTestCase {
	@Resource
	private AddressMapper mapper;
	
	@Test
	public void testInsert() {
		Address address = new Address();
		address.setUid(1);
		address.setName("赵六");
		Integer rows = mapper.insert(address);
		System.err.println("rows=" + rows);
		System.err.println("id=" + address.getAid());
	}
	
	@Test
	public void testCountByUid() {
		int uid = 1;
		int count = mapper.countByUid(uid);
		System.err.println("count:"+count);
	}
}
