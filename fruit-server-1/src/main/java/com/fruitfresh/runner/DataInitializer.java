package com.fruitfresh.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fruitfresh.entity.User;
import com.fruitfresh.enums.UserStatus;
import com.fruitfresh.enums.UserType;
import com.fruitfresh.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化：首次启动自动创建管理员和演示账号（避免手工往数据库写加密密码）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUser("admin", "admin123", "果漾管理员", UserType.ADMIN);
        initUser("demo", "123456", "爱吃水果的小果", UserType.NORMAL);
    }

    private void initUser(String username, String rawPassword, String nickname, UserType userType) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (count > 0) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setNickname(nickname);
        user.setUserType(userType.getCode());
        user.setStatus(UserStatus.NORMAL.getCode());
        userMapper.insert(user);
        log.info("已初始化账号: {} / {}", username, rawPassword);
    }
}
