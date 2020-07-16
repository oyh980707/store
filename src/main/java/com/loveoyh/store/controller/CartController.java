package com.loveoyh.store.controller;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.vo.CartVO;
import com.loveoyh.store.service.CartService;
import com.loveoyh.store.util.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("carts")
public class CartController extends BaseController {
	
	@Resource
	private CartService cartService;
	
	@RequestMapping("add_to_cart")
	public JsonResult addToCart(Cart cart,HttpSession session){
		// 从Session中获取uid和username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		// 调用业务层对象执行加入购物车
		cartService.addToCart(cart, uid, username);

		return JsonResult.newInstance();
	}
	
	@GetMapping("/")
	public JsonResult get(HttpSession session){
		// 从Session中获取uid
		Integer uid = getUidFromSession(session);
		
		List<CartVO> list = cartService.getByUid(uid);
		
		return JsonResult.newInstance(list);
	}
	
	@GetMapping("get_by_cids")
	public JsonResult getByCids(Integer[] cids,HttpSession session){
		//判断cids是否为null
		if(cids == null) {
			JsonResult jr = new JsonResult();
			jr.setState(SUCCESS);
			return jr;
		}
		// 从Session中获取uid
		Integer uid = getUidFromSession(session);
		
		List<CartVO> list = cartService.getByCids(cids, uid);
		
		return JsonResult.newInstance(list);
	}
	
	@RequestMapping("{cid}/increase")
	public JsonResult increase(@PathVariable("cid") Integer cid,HttpSession session){
		// 从Session中获取username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		
		Integer num = cartService.increase(cid, uid, username);
		
		return JsonResult.newInstance(num);
	}
	
	@RequestMapping("{cid}/reduce")
	public JsonResult reduce(@PathVariable("cid") Integer cid,HttpSession session){
		// 从Session中获取username
		Integer uid = getUidFromSession(session);
		String username = getUsernameFromSession(session);
		
		Integer num = cartService.reduce(cid, uid, username);
		
		return JsonResult.newInstance(num);
	}
	
	
}
