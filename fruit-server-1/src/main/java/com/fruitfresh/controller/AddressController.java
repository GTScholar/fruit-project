package com.fruitfresh.controller;

import com.fruitfresh.common.Result;
import com.fruitfresh.dto.AddressSaveDTO;
import com.fruitfresh.entity.Address;
import com.fruitfresh.security.SecurityUtil;
import com.fruitfresh.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收货地址接口
 */
@Tag(name = "收货地址接口")
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "我的地址列表")
    @GetMapping("/list")
    public Result<List<Address>> list() {
        return Result.success(addressService.list(SecurityUtil.getCurrentUserId()));
    }

    @Operation(summary = "新增地址")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody AddressSaveDTO dto) {
        addressService.save(SecurityUtil.getCurrentUserId(), dto);
        return Result.success();
    }

    @Operation(summary = "修改地址（需在请求体中带上 id）")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody AddressSaveDTO dto) {
        addressService.update(SecurityUtil.getCurrentUserId(), dto);
        return Result.success();
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(SecurityUtil.getCurrentUserId(), id);
        return Result.success();
    }

    @Operation(summary = "设为默认地址")
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(SecurityUtil.getCurrentUserId(), id);
        return Result.success();
    }
}
