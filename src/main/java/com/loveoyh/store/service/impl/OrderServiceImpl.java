package com.loveoyh.store.service.impl;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.entity.Order;
import com.loveoyh.store.entity.OrderItem;
import com.loveoyh.store.entity.vo.CartVO;
import com.loveoyh.store.mapper.OrderMapper;
import com.loveoyh.store.service.AddressService;
import com.loveoyh.store.service.CartService;
import com.loveoyh.store.service.OrderItemService;
import com.loveoyh.store.service.OrderService;
import com.loveoyh.store.service.ex.InsertException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
/**
 * 处理订单数据业务类
 * @author oyh
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	
	@Resource
	private OrderMapper orderMapper;
	
	@Resource
	private CartService cartService;
	
	@Resource
	private AddressService addressService;
	
	@Resource
	private OrderItemService orderItemService;
	
	/**
	 * 创建订单的业务流程：(事务操作)
	 * 1.创建当前时间对象：now
	 * 2.根据参数cids查询对应的购物车数据，得到List<CartVO>对象
	 * 	并遍历以上查询到的对象，根据各元素的price和num计算得到总价
	 * 3.根据收货地址aid查询收货地址详情
	 * 4.创建order对象，并封装order对象中的属性值：uid,recv_name,recv_phone,recv_address,total_price,state(0),order_time(now),pay_time(null),日志
	 * 5.插入订单数据：insertOrder(order)
	 * 6.遍历以上查询得到的List<CartVO>对象，创建orderItem对象
	 * 	并封装orderItem对象中的属性值：oid,gid,price,title,image,num,日志
	 * 	插入订单商品数据：insertOrderItem(itemItem)
	 */
	@Override
	@Transactional
	public Order create(Integer aid, Integer[] cids, Integer uid, String username) throws InsertException {
		Date now = new Date();
		
		List<CartVO> cartsList = cartService.getByCids(cids, uid);
		long totalPrice = 0L;
		for (CartVO item : cartsList) {
			totalPrice += item.getPrice() * item.getNum();
		}
		
		Address address = addressService.getByAid(aid);
		
		Order order = new Order();
		order.setUid(uid);
		order.setRecvAddress(address.getProvinceName() + address.getCityName() + address.getAreaName() + address.getAddress());
		order.setRecvName(address.getName());
		order.setRecvPhone(address.getPhone());
		order.setTotalPrice(totalPrice);
		order.setState(0);//未支付
		order.setOrderTime(now);
		order.setPayTime(null);
		order.setCreatedUser(username);
		order.setCreatedTime(now);
		order.setModifiedUser(username);
		order.setModifiedTime(now);
		
		insertOrder(order);
		
		for (CartVO item : cartsList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOid(order.getOid());//插入操作Mybatis返回给Order对象的oid
			orderItem.setGid(item.getGid());
			orderItem.setTitle(item.getTitle());
			orderItem.setImage(item.getImage());
			orderItem.setPrice(item.getPrice());
			orderItem.setNum(item.getNum());
			orderItem.setCreatedUser(username);
			orderItem.setCreatedTime(now);
			orderItem.setModifiedUser(username);
			orderItem.setModifiedTime(now);
			
			insertOrderItem(orderItem);
		}
		
		// TODO 删除购物车中对应的数据
		
		// TODO 修改对应的商品的库存量
		
		return order;
	}
	
	@Override
	public List<Order> queryByUid(Integer uid) {
		return this.orderMapper.queryByUid(uid);
	}
	
	@Override
	public List<Order> convertOrderList(List<Order> orders) {
		orders.stream().forEach(this::convertOrderList);
		return orders;
	}
	
	@Override
	public Order convertOrderList(Order order) {
		List<OrderItem> orderItems = orderItemService.queryByOid(order.getOid());
		if(null != orderItems && !orderItems.isEmpty()){
			order.setOrderItems(orderItems);
		}
		return order;
	}
	
	/**
	 * 插入订单数据
	 * @param order 订单数据对象
	 */
	private void insertOrder(Order order) {
		int rows = orderMapper.insertOrder(order);
		if(rows != 1) {
			throw new InsertException("Insert order data failed!");
		}
	};
	
	/**
	 * 插入订单商品数据
	 * @param orderItem 订单商品数据对象
	 */
	private void insertOrderItem(OrderItem orderItem) {
		int rows = orderMapper.insertOrderItem(orderItem);
		if(rows != 1) {
			throw new InsertException("Insert order item data failed!");
		}
	};
	
}
