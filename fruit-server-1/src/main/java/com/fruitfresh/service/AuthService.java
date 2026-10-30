package com.fruitfresh.service;

import com.fruitfresh.dto.LoginDTO;
import com.fruitfresh.dto.RegisterDTO;
import com.fruitfresh.vo.LoginVO;

/**
 * 认证服务接口：注册、登录
 */
public interface AuthService {

    /** 注册（默认普通用户） */
    void register(RegisterDTO dto);

    /** 登录：校验用户名密码，签发 JWT */
    LoginVO login(LoginDTO dto);
}
