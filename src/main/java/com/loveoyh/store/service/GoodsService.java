package com.loveoyh.store.service;

import com.loveoyh.store.entity.Goods;

import java.util.List;

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
	List<Goods> getHotList();
	
	/**
	 * 通过商品id获取商品信息
	 * @param id 商品id
	 * @return 商品数据对象
	 */
	Goods getById(Long id);
	
	/**
	 * 获取最新商品列表
	 * @return
	 */
	List<Goods> getNewList();
	
	/**
	 * 更新商品信息
	 * @param goods
	 * @return
	 */
	int update(Goods goods);
}
