package com.loveoyh.store.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 * @author oyh
 *
 */
@Getter
@Setter
public class Order extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	private Integer oid;
	private Integer uid;
	private String recvName;
	private String recvPhone;
	private String recvAddress;
	private Long totalPrice;
	private Integer state;
	private Date orderTime;
	private Date payTime;
	
	// 冗余字段
	List<OrderItem> orderItems;
	
}
