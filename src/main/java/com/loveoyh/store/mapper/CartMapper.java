package com.loveoyh.store.mapper;

import com.loveoyh.store.entity.Cart;
import com.loveoyh.store.entity.vo.CartVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 购物车数据接口
 * @author oyh
 *
 */
public interface CartMapper {
	/**
	 * 插入购物车数据
	 * @param cart 购物车数据
	 * @return 受影响的行数
	 */
	Integer insert(Cart cart);
	
	/**
	 * 根据用户id和商品id查询购物车数据
	 * @param uid 用户id
	 * @param gid 商品id
	 * @return 购物车数据，包含cid和num
	 */
	Cart findByUidAndGid(@Param("uid") Integer uid, @Param("gid") Long gid);
	
	/**
	 * 通过用户id查询有关显示购物车相关的数据
	 * @param uid 用户id
	 * @return 购物车相关数据，通过类CartVO携带数据
	 */
	List<CartVO> findByUid(Integer uid);
	
	/**
	 * 通过购物车id查询有关显示购物车相关的数据
	 * @param cid 购物车数据id
	 * @return 匹配购物车数据，如果没有匹配的数据则返回null
	 */
	Cart findByCid(Integer cid);
	
	/**
	 * 通过多个购物车id查询有关显示购物车相关的数据集合
	 * @param cids 多个购物车数据id
	 * @return匹配购物车数据集合，如果没有匹配的数据则返回null
	 */
	List<CartVO> findByCids(Integer[] cids);
	
	/**
	 * 修改购物车中商品的数量
	 * @param cid 购物车id
	 * @param num 商品数量
	 * @param modifiedUser 修改用户
	 * @param modifiedTime 修改时间
	 * @return 受影响的行数
	 */
	Integer updateNum(@Param("cid") Integer cid, @Param("num") Integer num, @Param("modifiedUser") String modifiedUser, @Param("modifiedTime") Date modifiedTime);
	
	/**
	 * 根据id删除商品购物车
	 * @param cid 购物车id
	 * @return 受影响的行数
	 */
	int remove(Integer cid);
}
