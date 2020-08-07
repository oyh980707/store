package com.loveoyh.store.controller;

import com.loveoyh.store.entity.JsonResult;
import com.loveoyh.store.entity.Order;
import com.loveoyh.store.service.OrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController extends BaseController {
	@Resource
	private OrderService orderService;
	
	@RequestMapping("create")
	public JsonResult create(Integer aid,Integer[] cids,HttpSession session){
		//获取用户id和用户名
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		
		Order order = orderService.create(aid, cids, uid, username);
		
		return JsonResult.newInstance(order);
	}
	
	@RequestMapping("list")
	public JsonResult list(HttpSession session){
		List<Order> orders = this.orderService.queryByUid(getUidFromSession(session));
		return JsonResult.newInstance(this.orderService.convertOrderList(orders));
	}
	
	@RequestMapping("/{id}")
	public JsonResult queryByid(@PathVariable("id") Long id){
		return JsonResult.newInstance(this.orderService.queryById(id));
	}
	
}
