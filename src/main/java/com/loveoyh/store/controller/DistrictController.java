package com.loveoyh.store.controller;

import com.loveoyh.store.entity.District;
import com.loveoyh.store.service.DistrictService;
import com.loveoyh.store.util.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("districts")
public class DistrictController extends BaseController {
	
	@Resource
	private DistrictService districtService;
	
	@GetMapping("/")
	public JsonResult get(String parent){
		List<District> districts = districtService.getByParent(parent);
		return JsonResult.newInstance(districts);
	}
}
