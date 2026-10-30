package com.fruitfresh.service;

import com.fruitfresh.dto.AddressSaveDTO;
import com.fruitfresh.entity.Address;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService {

    /** 我的地址列表（默认地址排最前） */
    List<Address> list(Long userId);

    /** 新增地址：若设为默认，则取消其他默认地址 */
    void save(Long userId, AddressSaveDTO dto);

    /** 修改地址 */
    void update(Long userId, AddressSaveDTO dto);

    /** 删除地址 */
    void delete(Long userId, Long addressId);

    /** 设为默认地址 */
    void setDefault(Long userId, Long addressId);
}
