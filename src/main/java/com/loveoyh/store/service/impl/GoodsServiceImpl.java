package com.loveoyh.store.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.loveoyh.store.entity.District;
import com.loveoyh.store.entity.Goods;
import com.loveoyh.store.mapper.DistrictMapper;
import com.loveoyh.store.mapper.GoodsMapper;
import com.loveoyh.store.service.DistrictService;
import com.loveoyh.store.service.GoodsService;
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
