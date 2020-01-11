package com.loveoyh.store.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.entity.District;
import com.loveoyh.store.mapper.AddressMapper;
import com.loveoyh.store.mapper.DistrictMapper;
import com.loveoyh.store.service.AddressService;
import com.loveoyh.store.service.DistrictService;
import com.loveoyh.store.service.ex.AddressCountLimitException;
import com.loveoyh.store.service.ex.InsertException;

@Service
public class AddressServiceImpl implements AddressService {
	
	@Resource
	private AddressMapper addressMapper;
	@Resource
	private DistrictService districtService;
	
	/**
	 * 增加收货地址业务流程：
	 * 1.根据参数uid查询当前用户的收货地址数量并判断收货地址数量是否达到上限值ADDRESS_MAX_COUNT
	 * 	是：抛出：AddressCountLimitException
	 * 2.判断当前用户的收货地址数量是否为0，并决定is_default的值，补全数据：is_default
	 * 3.补全数据：uid，province_name, city_name, area_name，时间对象....
	 * 4.插入收货地址数据
	 */
	@Override
	public void addnew(Address address, Integer uid, String username)
			throws AddressCountLimitException, InsertException {
		int count = countByUid(uid);
		if(count > ADDRESS_MAX_COUNT) {
			throw new AddressCountLimitException("out of range!");
		}
		Integer isDefault = count == 0 ? 1 : 0;
		address.setIsDefault(isDefault);
		
		address.setUid(uid);
		
		//补全provinceName...
		District province = districtService.getByCode(address.getProvinceCode());
		District city = districtService.getByCode(address.getCityCode());
		District area = districtService.getByCode(address.getAreaCode());
		if(province != null) {
			address.setProvinceName(province.getName());			
		}else {
			address.setProvinceCode(null);
			address.setProvinceName(null);
		}
		if(city != null) {
			address.setCityName(city.getName());		
		}else {
			address.setCityCode(null);
			address.setCityName(null);
		}
		if(area != null) {
			address.setAreaName(area.getName());		
		}else {
			address.setAreaCode(null);
			address.setAreaName(null);
		}
		
		// 封装user中的4个日志属性
		Date now = new Date();
		address.setCreatedUser(username);
		address.setCreatedTime(now);
		address.setModifiedUser(username);
		address.setModifiedTime(now);
		System.err.println(address);
		insert(address);
	}
	
	private void insert(Address address) {
		Integer rows = addressMapper.insert(address);
		if (rows != 1) {
			throw new InsertException("Failed to add shipping address! An unknown error occurred while inserting data!");
		}
	}

	private Integer countByUid(Integer uid) {
		// TODO uid用户验证
		
		Integer count = addressMapper.countByUid(uid);
		return count;
	}
}
