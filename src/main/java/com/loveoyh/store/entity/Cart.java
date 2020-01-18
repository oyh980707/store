package com.loveoyh.store.entity;
/**
 * 购物车实体类
 * @author oyh
 *
 */
public class Cart extends BaseEntity {
	private static final long serialVersionUID = 1L;
	private int cid;
	private int uid;
	private long gid;
	private int num;
	public Cart() {
	}
	public Cart(int cid, int uid, long gid, int num) {
		this.cid = cid;
		this.uid = uid;
		this.gid = gid;
		this.num = num;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cid;
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
		Cart other = (Cart) obj;
		if (cid != other.cid)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Cart [cid=" + cid + ", uid=" + uid + ", gid=" + gid + ", num=" + num + "]";
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public long getGid() {
		return gid;
	}
	public void setGid(long gid) {
		this.gid = gid;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
