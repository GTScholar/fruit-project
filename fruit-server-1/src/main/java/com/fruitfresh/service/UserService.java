package com.fruitfresh.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.dto.PasswordDTO;
import com.fruitfresh.dto.UserUpdateDTO;
import com.fruitfresh.entity.User;

/**
 * 用户服务接口：用户中心 + 管理端用户管理
 */
public interface UserService {

    /** 当前登录用户信息 */
    User getCurrentUser();

    /** 更新个人信息（只更新非空字段） */
    void updateInfo(UserUpdateDTO dto);

    /** 修改密码 */
    void updatePassword(PasswordDTO dto);

    /** 管理端：用户分页查询（仅普通用户，可按用户名/昵称搜索） */
    Page<User> adminPage(Integer pageNum, Integer pageSize, String keyword);

    /** 管理端：启用/禁用用户 */
    void updateStatus(Long userId, Integer status);
}
