package com.fruitfresh.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.Result;
import com.fruitfresh.dto.ProductSaveDTO;
import com.fruitfresh.entity.Product;
import com.fruitfresh.service.ProductService;
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
 * 商品管理（管理端）
 */
@Tag(name = "商品管理(管理端)")
@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "商品分页查询")
    @GetMapping("/page")
    public Result<Page<Product>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer status) {
        return Result.success(productService.adminPage(pageNum, pageSize, categoryId, keyword, status));
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody ProductSaveDTO dto) {
        productService.save(dto);
        return Result.success();
    }

    @Operation(summary = "修改商品（需在请求体中带上 id）")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ProductSaveDTO dto) {
        productService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }

    @Operation(summary = "上架/下架")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.toggleStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "设置/取消热销")
    @PutMapping("/{id}/hot")
    public Result<Void> toggleHot(@PathVariable Long id, @RequestParam Integer hot) {
        productService.toggleHot(id, hot);
        return Result.success();
    }
}
