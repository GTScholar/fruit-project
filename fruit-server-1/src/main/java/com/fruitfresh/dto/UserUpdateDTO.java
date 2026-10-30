package com.fruitfresh.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新个人信息请求
 */
@Data
public class UserUpdateDTO {

    @Size(max = 20, message = "昵称最长20个字符")
    private String nickname;

    private String avatar;

    @Size(max = 20, message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
}
