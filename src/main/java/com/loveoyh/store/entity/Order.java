package com.loveoyh.store.entity;

import java.util.Date;
/**
 * 订单实体类
 * @author oyh
 *
 */
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
	public Order() {
	}
	public Order(Integer oid, Integer uid, String recvName, String recvPhone, String recvAddress, Long totalPrice,
			Integer state, Date orderTime, Date payTime) {
		this.oid = oid;
		this.uid = uid;
		this.recvName = recvName;
		this.recvPhone = recvPhone;
		this.recvAddress = recvAddress;
		this.totalPrice = totalPrice;
		this.state = state;
		this.orderTime = orderTime;
		this.payTime = payTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((oid == null) ? 0 : oid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (oid == null) {
			if (other.oid != null)
				return false;
		} else if (!oid.equals(other.oid))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Order [oid=" + oid + ", uid=" + uid + ", recvName=" + recvName + ", recvPhone=" + recvPhone
				+ ", recvAddress=" + recvAddress + ", totalPrice=" + totalPrice + ", state=" + state + ", orderTime="
				+ orderTime + ", payTime=" + payTime + "]";
	}
	public Integer getOid() {
		return oid;
	}
	public void setOid(Integer oid) {
		this.oid = oid;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getRecvName() {
		return recvName;
	}
	public void setRecvName(String recvName) {
		this.recvName = recvName;
	}
	public String getRecvPhone() {
		return recvPhone;
	}
	public void setRecvPhone(String recvPhone) {
		this.recvPhone = recvPhone;
	}
	public String getRecvAddress() {
		return recvAddress;
	}
	public void setRecvAddress(String recvAddress) {
		this.recvAddress = recvAddress;
	}
	public Long getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Long totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
}
