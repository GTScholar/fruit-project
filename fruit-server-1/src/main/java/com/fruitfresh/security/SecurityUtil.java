package com.fruitfresh.security;

import com.fruitfresh.common.BusinessException;
import com.fruitfresh.common.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具：获取当前登录用户信息
 */
public class SecurityUtil {

    private SecurityUtil() {
    }

    /** 获取当前登录用户ID */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser.getUserId();
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
    }

    /** 获取当前登录用户对象 */
    public static LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser;
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
    }

    /** 当前用户是否为管理员 */
    public static boolean isAdmin() {
        LoginUser loginUser = getCurrentUser();
        return loginUser.getUserType() != null && loginUser.getUserType() == 2;
    }
}
