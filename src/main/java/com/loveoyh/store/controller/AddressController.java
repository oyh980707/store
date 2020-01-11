package com.loveoyh.store.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.service.AddressService;
import com.loveoyh.store.util.JsonResult;

@RestController
@RequestMapping("addresses")
public class AddressController extends BaseController{
	@Resource 
	private AddressService addressService;
	
	@RequestMapping("addnew")
	public JsonResult<Void> addnew(Address address,HttpSession session){
		// 从Session中获取uid和username
		int uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 调用业务层对象执行增加
		addressService.addnew(address, uid, username);
		// 响应成功
		return new JsonResult<Void>(SUCCESS);
	}
	
}
