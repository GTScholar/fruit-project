package com.fruitfresh.controller;

import com.fruitfresh.common.Result;
import com.fruitfresh.dto.PasswordDTO;
import com.fruitfresh.dto.UserUpdateDTO;
import com.fruitfresh.entity.User;
import com.fruitfresh.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户中心接口
 */
@Tag(name = "用户中心")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public Result<User> info() {
        return Result.success(userService.getCurrentUser());
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/update")
    public Result<Void> update(@Valid @RequestBody UserUpdateDTO dto) {
        userService.updateInfo(dto);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO dto) {
        userService.updatePassword(dto);
        return Result.success();
    }
}
