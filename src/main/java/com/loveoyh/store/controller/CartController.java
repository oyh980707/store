package com.loveoyh.store.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.District;
import com.loveoyh.store.entity.vo.CartVO;
import com.loveoyh.store.service.CartService;
import com.loveoyh.store.service.DistrictService;
import com.loveoyh.store.util.JsonResult;

@RestController
@RequestMapping("carts")
public class CartController extends BaseController {
	
	@Resource
	private CartService cartService;
	
	@RequestMapping("add_to_cart")
	public JsonResult<Void> addToCart(Cart cart,HttpSession session){
		// 从Session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 调用业务层对象执行加入购物车
		cartService.addToCart(cart, uid, username);
		
		JsonResult<Void> jr = new JsonResult<Void>();
		jr.setState(SUCCESS);
		return jr;
	}
	
	@GetMapping("/")
	public JsonResult<List<CartVO>> get(HttpSession session){
		// 从Session中获取uid
		Integer uid = getUidFromSession(session);
		
		List<CartVO> list = cartService.getByUid(uid);
		
		return new JsonResult<List<CartVO>>(list);
	}
	
	@GetMapping("get_by_cids")
	public JsonResult<List<CartVO>> getByCids(Integer[] cids,HttpSession session){
		//判断cids是否为null
		if(cids == null) {
			JsonResult<List<CartVO>> jr = new JsonResult<List<CartVO>>();
			jr.setState(SUCCESS);
			return jr;
		}
		// 从Session中获取uid
		Integer uid = getUidFromSession(session);
		
		List<CartVO> list = cartService.getByCids(cids, uid);
		
		return new JsonResult<List<CartVO>>(list);
	}
	
	@RequestMapping("{cid}/increase")
	public JsonResult<Integer> increase(@PathVariable("cid") Integer cid,HttpSession session){
		// 从Session中获取username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		
		Integer num = cartService.increase(cid, uid, username);
		
		JsonResult<Integer> jr =  new JsonResult<Integer>(SUCCESS);
		jr.setData(num);
		return jr;
	}
	
	@RequestMapping("{cid}/reduce")
	public JsonResult<Integer> reduce(@PathVariable("cid") Integer cid,HttpSession session){
		// 从Session中获取username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		
		Integer num = cartService.reduce(cid, uid, username);
		
		JsonResult<Integer> jr =  new JsonResult<Integer>(SUCCESS);
		jr.setData(num);
		return jr;
	}
	
	
}
