package com.loveoyh.store.mapper;

import java.util.Date;
import java.util.List;

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
	
	@Test
	public void testFindByUid() {
		Integer uid = 1;
		List<Address> list = mapper.findByUid(uid);
		System.err.println("BEGIN:");
		for (Address address : list) {
			System.err.println(address);
		}
		System.err.println("END.");
	}
	
	@Test
	public void testFindByAid() {
		Integer aid = 22;
		Address address = mapper.findByAid(aid);
		if(address != null) {
			System.err.println("address:"+address);			
		}
		System.err.println("OK.");
	}
	
	@Test
	public void testUpdateNonDefault() {
		Integer uid = 1;
		int rows = mapper.updateNonDefault(uid);
		System.err.println("rows:"+rows);
	}
	
	@Test
	public void testUpdateDefault() {
		Integer aid = 22;
		String modifiedUser = "root";
		Date modifiedTime = new Date();
		int rows = mapper.updateDefault(aid, modifiedUser, modifiedTime);
		System.err.println("rows:"+rows);
	}
	
	@Test
	public void testFindLastModified() {
		Integer uid = 1;
		Address address = mapper.findLastModified(uid);
		System.err.println("address:"+address);
		System.err.println("OK.");
	}
	
	@Test
	public void testDeleteByAid() {
		Integer aid = 24;
		int rows = mapper.deleteByAid(aid);
		System.err.println("rows:"+rows);
		System.err.println("OK.");
	}
}
