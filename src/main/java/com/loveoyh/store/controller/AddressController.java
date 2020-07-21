package com.loveoyh.store.controller;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.service.AddressService;
import com.loveoyh.store.util.JsonResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("api/addresses")
public class AddressController extends BaseController{
	@Resource 
	private AddressService addressService;
	
	@PostMapping("addnew")
	public JsonResult addnew(Address address,HttpSession session){
		// 从Session中获取uid和username
		int uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 调用业务层对象执行增加
		addressService.addnew(address, uid, username);
		// 响应成功
		return JsonResult.newInstance();
	}
	
	@GetMapping("/")
	public JsonResult getByUid(HttpSession session){
		//从session中获取uid
		Integer uid = getUidFromSession(session);
		List<Address> addresses = addressService.getByUid(uid);
		return JsonResult.newInstance(addresses);
	}
	
	@RequestMapping("{aid}/set_default")
	public JsonResult setDefault(@PathVariable("aid") Integer aid,HttpSession session){
		String username = getUsernameFromSession(session);
		Integer uid = getUidFromSession(session);
		addressService.setDefault(aid, uid, username);
		
		return JsonResult.newInstance();
	}
	
	@RequestMapping("{aid}/delete")
	public JsonResult delete(@PathVariable("aid") Integer aid,HttpSession session){
		String username = getUsernameFromSession(session);
		Integer uid = getUidFromSession(session);
		addressService.delete(aid, uid, username);
		return JsonResult.newInstance();
	}
}
