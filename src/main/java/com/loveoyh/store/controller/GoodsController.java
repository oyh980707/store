package com.loveoyh.store.controller;

import com.loveoyh.store.service.GoodsService;
import com.loveoyh.store.entity.JsonResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/goods")
public class GoodsController extends BaseController {
	
	@Resource
	private GoodsService goodsService;
	
	@GetMapping("/hot")
	public JsonResult getHotList(String parent){
		return JsonResult.newInstance(goodsService.getHotList());
	}
	
	@GetMapping("/new")
	public JsonResult getNewList(){
		return JsonResult.newInstance(this.goodsService.getNewList());
	}
	
	@GetMapping("{id}/details")
	public JsonResult getById(@PathVariable("id") Long id){
		return JsonResult.newInstance(goodsService.getById(id));
	}
	
	@RequestMapping("/search")
	public JsonResult searchGoods(@RequestParam("search") String search){
		return JsonResult.newInstance(goodsService.searchGoods(search));
	}
}
