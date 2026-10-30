package com.fruitfresh.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruitfresh.common.BusinessException;
import com.fruitfresh.dto.LoginDTO;
import com.fruitfresh.dto.RegisterDTO;
import com.fruitfresh.entity.User;
import com.fruitfresh.enums.UserStatus;
import com.fruitfresh.enums.UserType;
import com.fruitfresh.mapper.UserMapper;
import com.fruitfresh.security.JwtUtil;
import com.fruitfresh.service.AuthService;
import com.fruitfresh.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现：注册、登录
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 注册（默认普通用户）
     */
    @Override
    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在，请换一个试试");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        // BCrypt 加密存储
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(StrUtil.isBlank(dto.getNickname()) ? dto.getUsername() : dto.getNickname());
        user.setUserType(UserType.NORMAL.getCode());
        user.setStatus(UserStatus.NORMAL.getCode());
        userMapper.insert(user);
    }

    /**
     * 登录：校验用户名密码，签发 JWT
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!UserStatus.NORMAL.is(user.getStatus())) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        return new LoginVO(token, user);
    }
}
