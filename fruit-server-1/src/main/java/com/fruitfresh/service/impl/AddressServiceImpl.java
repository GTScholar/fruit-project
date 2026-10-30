package com.fruitfresh.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruitfresh.common.BusinessException;
import com.fruitfresh.dto.AddressSaveDTO;
import com.fruitfresh.entity.Address;
import com.fruitfresh.enums.YesNo;
import com.fruitfresh.mapper.AddressMapper;
import com.fruitfresh.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务实现
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    /** 我的地址列表（默认地址排最前） */
    @Override
    public List<Address> list(Long userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getUpdateTime));
    }

    /** 新增地址：若设为默认，则取消其他默认地址 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long userId, AddressSaveDTO dto) {
        Address address = new Address();
        BeanUtil.copyProperties(dto, address, "id");
        address.setUserId(userId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(YesNo.NO.getCode());
        }
        // 第一条地址自动设为默认
        if (countByUser(userId) == 0) {
            address.setIsDefault(YesNo.YES.getCode());
        }
        if (YesNo.YES.is(address.getIsDefault())) {
            cancelDefault(userId);
        }
        addressMapper.insert(address);
    }

    /** 修改地址 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, AddressSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("地址ID不能为空");
        }
        getOwnedAddress(userId, dto.getId());

        Address address = new Address();
        BeanUtil.copyProperties(dto, address);
        // 归属关系不允许被请求体篡改
        address.setUserId(userId);

        if (YesNo.YES.is(address.getIsDefault())) {
            cancelDefault(userId);
        }
        addressMapper.updateById(address);
    }

    /** 删除地址 */
    @Override
    public void delete(Long userId, Long addressId) {
        Address exist = getOwnedAddress(userId, addressId);
        addressMapper.deleteById(exist.getId());
    }

    /** 设为默认地址 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long addressId) {
        Address exist = getOwnedAddress(userId, addressId);
        cancelDefault(userId);
        Address update = new Address();
        update.setId(exist.getId());
        update.setIsDefault(YesNo.YES.getCode());
        addressMapper.updateById(update);
    }

    /** 校验地址归属 */
    private Address getOwnedAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在");
        }
        return address;
    }

    private Long countByUser(Long userId) {
        return addressMapper.selectCount(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId));
    }

    private void cancelDefault(Long userId) {
        Address update = new Address();
        update.setIsDefault(YesNo.NO.getCode());
        addressMapper.update(update, new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, YesNo.YES.getCode()));
    }
}
