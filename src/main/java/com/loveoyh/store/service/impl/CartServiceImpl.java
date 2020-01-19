package com.loveoyh.store.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.vo.CartVO;
import com.loveoyh.store.mapper.CartMapper;
import com.loveoyh.store.service.CartService;
import com.loveoyh.store.service.ex.AccessDeniedException;
import com.loveoyh.store.service.ex.CartNotFoundException;
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
	 * 根据用户id查询所有的购物车商品
	 */
	@Override
	public List<CartVO> getByUid(Integer uid) {
		return findByUid(uid);
	}
	
	/**
	 * 通过多个购物车id查询有关显示购物车相关的数据集合业务流程
	 * 1.查询数据
	 * 2.逐一判断结果中的每一条数据，是否都是当前用户的数据
	 * 	如果不是当前用户的数据，移除该数据
	 * 3.返回数据
	 * 
	 * 注：
	 * 	此处涉及到元素的删除，使用迭代器来完成删除集合中的元素
	 */
	@Override
	public List<CartVO> getByCids(Integer[] cids, Integer uid) {
		List<CartVO> results = findByCids(cids);
		
		Iterator<CartVO> it = results.iterator();
		while(it.hasNext()) {
			if(uid != it.next().getUid()) {
				it.remove();
			}
		}
		return results;
	}
	
	
	/**
	 * 用户增加购物车商品的数量（即增加数量1）的业务流程：
	 * 1.根据参数cid查询购物车数据并判断查询结果是否为null
	 * 	是：抛出CartNotFoundException
	 * 2.判断查询结果中的uid与参数uid是否不匹配
	 * 	是：抛出AccessDeniedException
	 * 3.从查询结果中取出num，增加1，得到新的数量
	 * 4.更新商品数量
	 * 5.返回新的数量(newNum)
	 */
	@Override
	public Integer increase(Integer cid, Integer uid, String username)
			throws CartNotFoundException, AccessDeniedException, UpdateException {
		Cart cart = findByCid(cid);
		if(cart == null) {
			throw new CartNotFoundException("cart data does not exist!");
		}
		
		if(cart.getUid() != uid) {
			throw new AccessDeniedException("Operation object and address ownership are not consistent!");
		}
		
		Integer newNum = cart.getNum() + 1;
		updateNum(cid, newNum, username, new Date());
		
		return newNum;
	};
	
	/**
	 * 用户减少购物车商品的数量（即减少数量1）的业务流程：
	 * 1.根据参数cid查询购物车数据并判断查询结果是否为null
	 * 	是：抛出CartNotFoundException
	 * 2.判断查询结果中的uid与参数uid是否不匹配
	 * 	是：抛出AccessDeniedException
	 * 3.从查询结果中取出num，减小1，得到新的数量
	 * 4.判断是否小于等于0
	 * 	是：删除该购物车
	 * 5.更新商品数量
	 * 6.返回新的数量(newNum)
	 */
	@Override
	public Integer reduce(Integer cid, Integer uid, String username)
			throws CartNotFoundException, AccessDeniedException, UpdateException {
		Cart cart = findByCid(cid);
		if(cart == null) {
			throw new CartNotFoundException("cart data does not exist!");
		}
		
		if(cart.getUid() != uid) {
			throw new AccessDeniedException("Operation object and address ownership are not consistent!");
		}
		
		Integer newNum = cart.getNum() - 1;
		//判断是否小于等于0
		if(newNum <= 0) {
			//TODO 删除该商品
			return 0;
		}
		
		updateNum(cid, newNum, username, new Date());
		
		return newNum;
	}
	
	/**
	 * 通过购物车id查询有关显示购物车相关的数据
	 * @param cid 购物车数据id
	 * @return 匹配购物车数据，如果没有匹配的数据则返回null
	 */
	private Cart findByCid(Integer cid) {
		return cartMapper.findByCid(cid);
	}
	
	/**
	 * 插入购物车数据
	 * @param cart 购物车数据
	 * @throws InsertException
	 */
	private void insert(Cart cart) throws InsertException {
		Integer rows = cartMapper.insert(cart);
		if (rows != 1) {
			throw new InsertException("将商品添加到购物车失败！插入数据时出现未知错误！");
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
			throw new UpdateException("更新商品数量失败！更新数据时出现未知错误！");
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

	/**
	 * 通过用户id查询有关显示购物车相关的数据
	 * @param uid 用户id
	 * @return 购物车相关数据，通过类CartVO携带数据
	 */
	private List<CartVO> findByUid(Integer uid){
		return cartMapper.findByUid(uid);
	}
	
	/**
	 * 通过多个购物车id查询有关显示购物车相关的数据集合
	 * @param cids 多个购物车数据id
	 * @return 匹配购物车数据集合，如果没有匹配的数据则返回null
	 */
	private List<CartVO> findByCids(Integer[] cids){
		return cartMapper.findByCids(cids);
	}
	
}
