package com.loveoyh.store.controller;

import com.loveoyh.store.service.GoodsService;
import com.loveoyh.store.util.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("goods")
public class GoodsController extends BaseController {
	
	@Resource
	private GoodsService goodsService;
	
	@GetMapping("/hot")
	public JsonResult getHotList(String parent){
		return JsonResult.newInstance(goodsService.getHotList());
	}
	
	@GetMapping("{id}/details")
	public JsonResult getById(@PathVariable("id") Long id){
		return JsonResult.newInstance(goodsService.getById(id));
	}
}
