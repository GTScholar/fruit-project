package com.fruitfresh.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.Result;
import com.fruitfresh.dto.BannerSaveDTO;
import com.fruitfresh.entity.Banner;
import com.fruitfresh.service.BannerService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 轮播图管理（管理端）
 */
@Tag(name = "轮播图管理(管理端)")
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @Operation(summary = "轮播图分页查询")
    @GetMapping("/page")
    public Result<Page<Banner>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(bannerService.adminPage(pageNum, pageSize));
    }

    @Operation(summary = "新增轮播图")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody BannerSaveDTO dto) {
        bannerService.save(dto);
        return Result.success();
    }

    @Operation(summary = "修改轮播图（需在请求体中带上 id）")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody BannerSaveDTO dto) {
        bannerService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return Result.success();
    }

    @Operation(summary = "启用/停用轮播图")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        bannerService.toggleStatus(id, status);
        return Result.success();
    }
}
