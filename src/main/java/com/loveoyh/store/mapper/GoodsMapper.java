package com.loveoyh.store.mapper;

import com.loveoyh.store.entity.Goods;

import java.util.List;

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
	List<Goods> findHotList();
	
	/**
	 * 通过商品id获取商品信息
	 * @param id 商品id
	 * @return 商品数据对象
	 */
	Goods findById(Long id);
	
	/**
	 * 获取最新商品列表
	 * @return
	 */
	List<Goods> findNewList();
	
	/**
	 * 更新商品信息
	 * @param goods
	 * @return
	 */
	int update(Goods goods);
	
	/**
	 * 通过title模糊查询
	 * @param search
	 * @return
	 */
	List<Goods> findByTitle(String search);
}
