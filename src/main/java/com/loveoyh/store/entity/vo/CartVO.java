package com.loveoyh.store.entity.vo;

import java.io.Serializable;

/**
 * Cart 和 Goods类关联查询结果类
 * @author oyh
 *
 */
public class CartVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer cid;
	private Integer uid;
	private Long gid;
	private String title;
	private Long price;
	private String image;
	private Integer num;
	public CartVO() {
	}
	public CartVO(Integer cid, Integer uid, Long gid, String title, Long price, String image, Integer num) {
		this.cid = cid;
		this.uid = uid;
		this.gid = gid;
		this.title = title;
		this.price = price;
		this.image = image;
		this.num = num;
	}
	@Override
	public String toString() {
		return "CartVO [cid=" + cid + ", uid=" + uid + ", gid=" + gid + ", title=" + title + ", price=" + price
				+ ", image=" + image + ", num=" + num + "]";
	}
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Long getGid() {
		return gid;
	}
	public void setGid(Long gid) {
		this.gid = gid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
}
