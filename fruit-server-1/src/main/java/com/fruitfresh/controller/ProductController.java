package com.fruitfresh.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.Result;
import com.fruitfresh.entity.Product;
import com.fruitfresh.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品接口（用户端）
 */
@Tag(name = "商品接口")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品分页查询（支持分类、关键词搜索）")
    @GetMapping("/page")
    public Result<Page<Product>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "8") Integer pageSize,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) String keyword) {
        return Result.success(productService.page(pageNum, pageSize, categoryId, keyword));
    }

    @Operation(summary = "热销商品推荐")
    @GetMapping("/hot")
    public Result<List<Product>> hot() {
        return Result.success(productService.hotList());
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.detail(id));
    }
}
