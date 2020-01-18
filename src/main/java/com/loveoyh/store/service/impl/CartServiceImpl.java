package com.loveoyh.store.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.mapper.CartMapper;
import com.loveoyh.store.service.CartService;
import com.loveoyh.store.service.ex.InsertException;
import com.loveoyh.store.service.ex.UpdateException;
/**
 * 购物车业务层
 * @author oyh
 *
 */
@Service
public class CartServiceImpl implements CartService{
	
	@Resource
	private CartMapper cartMapper;

	/**
	 * 添加购物车业务功能流程：
	 * 1.创建时间对象
	 * 2.根据参数cart中封装的uid和gid执行查询
	 * 3.检查查询结果是否为null
	 * 	 是：1)封装购物车数据；2)执行插入数据
	 * 4.封装购物车数据（注：将以上获取的原数量与参数cart中的num相加，得到新的数量）
	 * 5.执行修改数量
	 */
	@Override
	public void addToCart(Cart cart, Integer uid, String username) throws InsertException, UpdateException {
		Date now = new Date();
		
		Cart cartTemp = findByUidAndGid(uid, cart.getGid());
		
		if(cartTemp == null) {
			cart.setUid(uid);
			cart.setCreatedTime(now);
			cart.setNum(1);
			cart.setCreatedUser(username);
			cart.setModifiedTime(now);
			cart.setModifiedUser(username);
			insert(cart);
			return;
		}
		
		int num = cartTemp.getNum()+cart.getNum();
		int cid = cartTemp.getCid();
		updateNum(cid, num, username, now);
	}
	
	/**
	 * 插入购物车数据
	 * @param cart 购物车数据
	 * @throws InsertException
	 */
	private void insert(Cart cart) throws InsertException {
		Integer rows = cartMapper.insert(cart);
		if (rows != 1) {
			throw new InsertException(
				"将商品添加到购物车失败！插入数据时出现未知错误！");
		}
	}
	
	/**
	 * 修改购物车中商品的数量
	 * @param cid 购物车数据的id
	 * @param num 新的商品数量
	 * @param modifiedUser 修改执行人
	 * @param modifiedTime 修改时间
	 * @throws UpdateException
	 */
	private void updateNum(Integer cid, Integer num, 
	    String modifiedUser, Date modifiedTime)
			throws UpdateException {
		Integer rows = cartMapper.updateNum(cid, num, modifiedUser, modifiedTime);
		if (rows != 1) {
			throw new UpdateException(
				"更新商品数量失败！更新数据时出现未知错误！");
		}
	}
	
	/**
	 * 根据用户id和商品id查询购物车数据
	 * @param uid 用户id
	 * @param gid 商品id
	 * @return 匹配的购物车数据，如果没有匹配的数据，则返回null
	 */
	private Cart findByUidAndGid(Integer uid, Long gid) {
		return cartMapper.findByUidAndGid(uid, gid);
	}
}
