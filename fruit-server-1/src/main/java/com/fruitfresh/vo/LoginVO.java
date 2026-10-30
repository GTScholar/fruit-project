package com.fruitfresh.vo;

import com.fruitfresh.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功返回：token + 用户信息
 */
@Data
@AllArgsConstructor
public class LoginVO implements Serializable {

    /** JWT token */
    private String token;

    /** 用户信息 */
    private User user;
}
