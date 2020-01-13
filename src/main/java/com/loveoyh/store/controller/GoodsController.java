package com.loveoyh.store.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loveoyh.store.entity.District;
import com.loveoyh.store.entity.Goods;
import com.loveoyh.store.service.DistrictService;
import com.loveoyh.store.service.GoodsService;
import com.loveoyh.store.util.JsonResult;

@RestController
@RequestMapping("goods")
public class GoodsController extends BaseController {
	
	@Resource
	private GoodsService goodsService;
	
	@GetMapping("/hot")
	public JsonResult<List<Goods>> getHotList(String parent){
		return new JsonResult<List<Goods>>(goodsService.getHotList());
	}
	
	@GetMapping("{id}/details")
	public JsonResult<Goods> getById(@PathVariable("id") Long id){
		return new JsonResult<Goods>(goodsService.getById(id));
	}
}
