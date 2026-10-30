package com.fruitfresh.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.Result;
import com.fruitfresh.entity.User;
import com.fruitfresh.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理（管理端）
 */
@Tag(name = "用户管理(管理端)")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "用户分页查询（仅普通用户）")
    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String keyword) {
        return Result.success(userService.adminPage(pageNum, pageSize, keyword));
    }

    @Operation(summary = "启用/禁用用户（status: 1启用 0禁用）")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
}
