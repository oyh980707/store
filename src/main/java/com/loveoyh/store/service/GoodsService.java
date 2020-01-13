package com.loveoyh.store.service;

import java.util.List;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.entity.District;
import com.loveoyh.store.entity.Goods;
import com.loveoyh.store.service.ex.AddressCountLimitException;
import com.loveoyh.store.service.ex.InsertException;

/**
 * 处理商品数据接口
 * @author oyh
 *
 */
public interface GoodsService {
	/**
	 * 获取热销商品列表
	 * @return 返回热销商品列表
	 */
	public List<Goods> getHotList();
	
	/**
	 * 通过商品id获取商品信息
	 * @param id 商品id
	 * @return 商品数据对象
	 */
	public Goods getById(Long id);
}
