package com.fruitfresh.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.BusinessException;
import com.fruitfresh.dto.PasswordDTO;
import com.fruitfresh.dto.UserUpdateDTO;
import com.fruitfresh.entity.User;
import com.fruitfresh.enums.UserStatus;
import com.fruitfresh.enums.UserType;
import com.fruitfresh.mapper.UserMapper;
import com.fruitfresh.security.SecurityUtil;
import com.fruitfresh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现：用户中心 + 管理端用户管理
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /** 当前登录用户信息 */
    @Override
    public User getCurrentUser() {
        User user = userMapper.selectById(SecurityUtil.getCurrentUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /** 更新个人信息（只更新非空字段） */
    @Override
    public void updateInfo(UserUpdateDTO dto) {
        User user = new User();
        user.setId(SecurityUtil.getCurrentUserId());
        user.setNickname(dto.getNickname());
        user.setAvatar(dto.getAvatar());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        userMapper.updateById(user);
    }

    /** 修改密码 */
    @Override
    public void updatePassword(PasswordDTO dto) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        User update = new User();
        update.setId(user.getId());
        update.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(update);
    }

    /** 管理端：用户分页查询（仅普通用户，可按用户名/昵称搜索） */
    @Override
    public Page<User> adminPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return userMapper.selectPage(page, new LambdaQueryWrapper<User>()
                .eq(User::getUserType, UserType.NORMAL.getCode())
                .and(StrUtil.isNotBlank(keyword), w -> w
                        .like(User::getUsername, keyword)
                        .or()
                        .like(User::getNickname, keyword))
                .orderByDesc(User::getCreateTime));
    }

    /** 管理端：启用/禁用用户 */
    @Override
    public void updateStatus(Long userId, Integer status) {
        if (UserStatus.of(status) == null) {
            throw new BusinessException("用户状态值不正确");
        }
        User exist = userMapper.selectById(userId);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        User update = new User();
        update.setId(userId);
        update.setStatus(status);
        userMapper.updateById(update);
    }
}
