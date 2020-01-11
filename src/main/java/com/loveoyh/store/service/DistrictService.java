package com.loveoyh.store.service;

import java.util.List;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.entity.District;
import com.loveoyh.store.service.ex.AddressCountLimitException;
import com.loveoyh.store.service.ex.InsertException;

/**
 * 处理省/市/区数据接口
 * @author oyh
 *
 */
public interface DistrictService {
	/**
	 * 根据父级代号获取全国所有省/某省所有市/某市所有区的列表
	 * @param parent 父级代号，如果尝试获取全国所有省，则代号应该使用"86"
	 * @return 全国所有省/某省所有市/某市所有区的列表
	 */
	public List<District> getByParent(String parent);
	
	/**
	 * 根据省/市/区代号查询详情
	 * @param code 省/市/区的代号
	 * @return 匹配的省/市/区的详情，如果没有返回null
	 */
	public District getByCode(String code);
}
