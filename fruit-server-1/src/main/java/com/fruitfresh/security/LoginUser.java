package com.fruitfresh.security;

import com.fruitfresh.entity.User;
import com.fruitfresh.enums.UserStatus;
import com.fruitfresh.enums.UserType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security 登录用户对象，包装数据库用户
 */
@Getter
public class LoginUser implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Integer userType;
    private final Integer status;

    public LoginUser(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.userType = user.getUserType();
        this.status = user.getStatus();
    }

    /** 角色：管理员 -> ROLE_ADMIN，普通用户 -> ROLE_USER */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = UserType.ADMIN.is(userType) ? "ROLE_ADMIN" : "ROLE_USER";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.NORMAL.is(status);
    }
}
