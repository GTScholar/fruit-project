package com.fruitfresh.controller;

import com.fruitfresh.common.Result;
import com.fruitfresh.entity.Category;
import com.fruitfresh.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类接口（用户端）
 */
@Tag(name = "分类接口")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "查询启用中的分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }
}
