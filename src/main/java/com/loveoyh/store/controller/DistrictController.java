package com.loveoyh.store.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loveoyh.store.entity.District;
import com.loveoyh.store.service.DistrictService;
import com.loveoyh.store.util.JsonResult;

@RestController
@RequestMapping("districts")
public class DistrictController extends BaseController {
	
	@Resource
	private DistrictService districtService;
	
	@GetMapping("/")
	public JsonResult<List<District>> get(String parent){
		List<District> districts = districtService.getByParent(parent);
		return new JsonResult<List<District>>(districts);
	}
}
