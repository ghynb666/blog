package com.blog.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.AppException;
import com.blog.common.ErrorCode;
import com.blog.entity.User;
import com.blog.enums.UserRole;
import com.blog.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private final UserService userService;

    public SaTokenConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    StpUtil.checkLogin();
                    Long userId = StpUtil.getLoginIdAsLong();
                    User user = userService.getById(userId);
                    if (user == null || !UserRole.ADMIN.name().equalsIgnoreCase(user.getRole())) {
                        throw new AppException(ErrorCode.ROLE_FORBIDDEN);
                    }
                }))
                .addPathPatterns("/api/admin/**", "/api/v1/admin/**")
                .excludePathPatterns(
                        "/api/admin/login",
                        "/api/admin/public-key",
                        "/api/admin/register",
                        "/api/v1/admin/login",
                        "/api/v1/admin/public-key",
                        "/api/v1/admin/register"
                );
    }
}
