package com.loveoyh.store.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	@PostMapping("addnew")
	public JsonResult<Void> addnew(Address address,HttpSession session){
		// 从Session中获取uid和username
		int uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 调用业务层对象执行增加
		addressService.addnew(address, uid, username);
		// 响应成功
		return new JsonResult<Void>(SUCCESS);
	}
	
	@GetMapping("/")
	public JsonResult<List<Address>> getByUid(HttpSession session){
		//从session中获取uid
		Integer uid = getUidFromSession(session);
		List<Address> addresses = addressService.getByUid(uid);
		return new JsonResult<List<Address>>(addresses);
	}
	
	@RequestMapping("{aid}/set_default")
	public JsonResult<Void> setDefault(@PathVariable("aid") Integer aid,HttpSession session){
		String username = getUsernameFromSession(session);
		Integer uid = getUidFromSession(session);
		addressService.setDefault(aid, uid, username);
		return new JsonResult<Void>(SUCCESS);
	}
	
	@RequestMapping("{aid}/delete")
	public JsonResult<Void> delete(@PathVariable("aid") Integer aid,HttpSession session){
		String username = getUsernameFromSession(session);
		Integer uid = getUidFromSession(session);
		addressService.delete(aid, uid, username);
		return new JsonResult<Void>();
	}
}
