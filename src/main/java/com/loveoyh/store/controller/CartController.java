package com.loveoyh.store.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
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
		
		return new JsonResult<Void>(SUCCESS);
	}
	
	@GetMapping("/")
	public JsonResult<List<CartVO>> get(HttpSession session){
		// 从Session中获取uid和username
		Integer uid = getUidFromSession(session);
		
		List<CartVO> list = cartService.getByUid(uid);
		
		return new JsonResult<List<CartVO>>(list);
	}
}
