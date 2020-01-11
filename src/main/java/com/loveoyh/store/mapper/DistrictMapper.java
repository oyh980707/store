package com.loveoyh.store.mapper;

import java.util.List;

import com.loveoyh.store.entity.District;

/**
 * 处理省/市/区数据接口
 * @author HP
 *
 */
public interface DistrictMapper {
	/**
	 * 根据父级代号获取全国所有省/某省所有市/某市所有区的列表
	 * @param parent 父级代号，如果尝试获取全国所有省，则代号应该使用"86"
	 * @return 全国所有省/某省所有市/某市所有区的列表
	 */
	public List<District> findByParent(String parent);
	
	/**
	 * 根据省/市/区代号查询详情
	 * @param code 省/市/区的代号
	 * @return 匹配的省/市/区的详情，如果没有返回null
	 */
	public District findByCode(String code);
}
