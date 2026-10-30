package com.fruitfresh.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.Result;
import com.fruitfresh.dto.CategorySaveDTO;
import com.fruitfresh.entity.Category;
import com.fruitfresh.service.CategoryService;
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
 * 分类管理（管理端）
 */
@Tag(name = "分类管理(管理端)")
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "分类分页查询")
    @GetMapping("/page")
    public Result<Page<Category>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String name) {
        return Result.success(categoryService.adminPage(pageNum, pageSize, name));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody CategorySaveDTO dto) {
        categoryService.save(dto);
        return Result.success();
    }

    @Operation(summary = "修改分类（需在请求体中带上 id）")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody CategorySaveDTO dto) {
        categoryService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    @Operation(summary = "启用/停用分类")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        categoryService.toggleStatus(id, status);
        return Result.success();
    }
}
