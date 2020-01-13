package com.loveoyh.store.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.service.ex.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressServiceTestCase{
	@Resource
	public AddressService service;
	
	@Test
	public void testAddnew() {
		try {
			Address address = new Address();
			address.setName("小V同学");
			int uid = 1;
			address.setUid(uid);
			String username = "root";
			service.addnew(address, uid, username);
			System.err.println("testAddnew() is OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void testFindByUid() {
		Integer uid = 1;
		List<Address> list = service.getByUid(uid);
		System.err.println("BEGIN:");
		for (Address address : list) {
			System.err.println(address);
		}
		System.err.println("END.");
	}
	
	@Test
	public void testSetDefault() {
		try {
			Integer aid = 12;
			Integer uid = 1;
			String username = "root";
			service.setDefault(aid, uid, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void testDelete() {
		try {
			Integer aid = 22;
			Integer uid = 1;
			String username = "root";
			service.delete(aid, uid, username);
			System.err.println("OK.");
		} catch (ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
}
