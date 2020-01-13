package com.loveoyh.store.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loveoyh.store.entity.Address;
import com.loveoyh.store.entity.District;
import com.loveoyh.store.mapper.AddressMapper;
import com.loveoyh.store.service.AddressService;
import com.loveoyh.store.service.DistrictService;
import com.loveoyh.store.service.ex.AccessDeniedException;
import com.loveoyh.store.service.ex.AddressCountLimitException;
import com.loveoyh.store.service.ex.AddressNotFoundException;
import com.loveoyh.store.service.ex.DeleteException;
import com.loveoyh.store.service.ex.InsertException;
import com.loveoyh.store.service.ex.UpdateException;

@Service
public class AddressServiceImpl implements AddressService {
	
	@Resource
	private AddressMapper addressMapper;
	@Resource
	private DistrictService districtService;
	
	/**
	 * 增加收货地址业务流程：
	 * 1.根据参数uid查询当前用户的收货地址数量并判断收货地址数量是否达到上限值ADDRESS_MAX_COUNT
	 * 	是：抛出：AddressCountLimitException
	 * 2.判断当前用户的收货地址数量是否为0，并决定is_default的值，补全数据：is_default
	 * 3.补全数据：uid，province_name, city_name, area_name，时间对象....
	 * 4.插入收货地址数据
	 */
	@Override
	public void addnew(Address address, Integer uid, String username)
			throws AddressCountLimitException, InsertException {
		int count = countByUid(uid);
		if(count > ADDRESS_MAX_COUNT) {
			throw new AddressCountLimitException("out of range!");
		}
		Integer isDefault = count == 0 ? 1 : 0;
		address.setIsDefault(isDefault);
		
		address.setUid(uid);
		
		//补全provinceName...
		District province = districtService.getByCode(address.getProvinceCode());
		District city = districtService.getByCode(address.getCityCode());
		District area = districtService.getByCode(address.getAreaCode());
		if(province != null) {
			address.setProvinceName(province.getName());			
		}else {
			address.setProvinceCode(null);
			address.setProvinceName(null);
		}
		if(city != null) {
			address.setCityName(city.getName());		
		}else {
			address.setCityCode(null);
			address.setCityName(null);
		}
		if(area != null) {
			address.setAreaName(area.getName());		
		}else {
			address.setAreaCode(null);
			address.setAreaName(null);
		}
		
		// 封装user中的4个日志属性
		Date now = new Date();
		address.setCreatedUser(username);
		address.setCreatedTime(now);
		address.setModifiedUser(username);
		address.setModifiedTime(now);
		
		insert(address);
	}
	
	/**
	 * 根据用户id获取所有的收货地址
	 */
	@Override
	public List<Address> getByUid(Integer uid) {
		return findByUid(uid);
	}
	
	/**
	 * 修改默认收货地址业务流程：(事务操作)
	 * 1.根据aid查询收货地址数据，判断结果是否为null
	 * 	是：抛出AddressNotFoundException
	 * 2.判断结果中的uid与参数uid是否不一致
	 * 	是：抛出AccessDeniedException
	 * 3.将该用户所有收货地址设置为非默认
	 * 4.将指定的收货地址设置为默认
	 */
	@Override
	@Transactional
	public void setDefault(Integer aid, Integer uid, String username)
			throws AddressNotFoundException, AccessDeniedException, UpdateException {
		Address address = findByAid(aid);
		if(address == null) {
			throw new AddressNotFoundException("The shipping address does not exist!");
		}
		
		if(address.getUid() != uid) {
			throw new AccessDeniedException("Operation object and address ownership are not consistent!");
		}
		
		updateNonDefault(uid);
		
		Date modifiedTime = new Date();
		updateDefault(aid, username, modifiedTime);
	}
	
	/**
	 * 删除收货地址业务流程：(事务操作)
	 * 1.根据aid查询收货地址数据,判断结果是否为null
	 * 	是：抛出AddressNotFoundException
	 * 2.判断结果中的uid与参数uid是否不一致
	 * 	是：抛出AccessDeniedException
	 * 3.执行删除
	 * 4.判断此前的查询结果中的isDefault是否为0,是则return;
	 * 5.统计当前用户的收货地址数量：countByUid()，判断剩余收货地址数量是否为0
	 * 	是则return;
	 * 6. 查询当前用户最近修改的收货地址，将最近修改的收货地址设置为默认
	 */
	@Override
	@Transactional
	public void delete(Integer aid, Integer uid, String username)
			throws AddressNotFoundException, AccessDeniedException, DeleteException {
		Address address = findByAid(aid);
		if(address == null) {
			throw new AddressNotFoundException("The shipping address to be deleted does not exist!");
		}
		
		if(address.getUid() != uid) {
			throw new AccessDeniedException("Operation object and address ownership are not consistent!");
		}
		
		delete(aid);
		
		if(address.getIsDefault() == 0) {
			return;
		}
		
		int count = countByUid(uid);
		if(count == 0) {
			return;
		}
		
		Address lastModifiedAddress = findLastModified(uid);
		Date modifiedTime = new Date();
		updateDefault(lastModifiedAddress.getAid(), username, modifiedTime);
	}
	
	private void insert(Address address) {
		Integer rows = addressMapper.insert(address);
		if (rows != 1) {
			throw new InsertException("Failed to add shipping address! An unknown error occurred while inserting data!");
		}
	}

	private Integer countByUid(Integer uid) {
		return addressMapper.countByUid(uid);
	}

	private List<Address> findByUid(Integer uid){
		return addressMapper.findByUid(uid);
	}
	
	private void updateNonDefault(Integer uid) throws UpdateException {
		Integer rows = addressMapper.updateNonDefault(uid);
		if (rows == 0) {
			throw new UpdateException("更新-将默认收货地址设为非默认地址，失败！");
		}
	}

	private void updateDefault(Integer aid, String modifiedUser, Date modifiedTime) throws UpdateException {
		Integer rows = addressMapper.updateDefault(aid, modifiedUser, modifiedTime);
		if (rows != 1) {
			throw new UpdateException("更新-将指定地址设为默认，失败！");
		}
	}

	private Address findByAid(Integer aid) {
		return addressMapper.findByAid(aid);
	}

	private void delete(Integer aid) {
		int rows = addressMapper.deleteByAid(aid);
		if(rows != 1) {
			throw new DeleteException("Failed to delete shipping address!");
		}
	}
	
	private Address findLastModified(Integer uid){
		return addressMapper.findLastModified(uid);
	}
}
