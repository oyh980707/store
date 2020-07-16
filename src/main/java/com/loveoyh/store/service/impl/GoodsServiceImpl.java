package com.loveoyh.store.service.impl;

import com.loveoyh.store.entity.Goods;
import com.loveoyh.store.mapper.GoodsMapper;
import com.loveoyh.store.service.GoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * 处理商品数据业务类
 * @author oyh
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService{
	
	@Resource
	private GoodsMapper goodsMapper;
	
	/**
	 * 获取热销商品业务流程：
	 * 1.返回商品列表
	 */
	@Override
	public List<Goods> getHotList() {
		return findHotList();
	}
	
	/**
	 * 通过id获取商品详细信息的业务流程：
	 * 1.返回商品商品对象
	 */
	@Override
	public Goods getById(Long id) {
		return findById(id);
	};
	
	private List<Goods> findHotList(){
		return goodsMapper.findHotList();
	}
	
	private Goods findById(Long id) {
		return goodsMapper.findById(id);
	};
}
