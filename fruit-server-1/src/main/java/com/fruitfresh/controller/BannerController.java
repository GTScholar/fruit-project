package com.fruitfresh.controller;

import com.fruitfresh.common.Result;
import com.fruitfresh.entity.Banner;
import com.fruitfresh.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图接口（用户端）
 */
@Tag(name = "轮播图接口")
@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "查询启用中的轮播图")
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.success(bannerService.list());
    }
}
