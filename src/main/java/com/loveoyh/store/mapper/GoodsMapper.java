package com.loveoyh.store.mapper;

import java.util.List;

import com.loveoyh.store.entity.District;
import com.loveoyh.store.entity.Goods;

/**
 * 处理商品数据接口
 * @author HP
 *
 */
public interface GoodsMapper {
	/**
	 * 获取热销商品列表
	 * @return 返回热销商品列表
	 */
	public List<Goods> findHotList();
	
	/**
	 * 通过商品id获取商品信息
	 * @param id 商品id
	 * @return 商品数据对象
	 */
	public Goods findById(Long id);
}
