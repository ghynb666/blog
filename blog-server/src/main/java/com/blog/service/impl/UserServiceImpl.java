package com.blog.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.common.AppException;
import com.blog.common.ErrorCode;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.entity.User;
import com.blog.enums.UserRole;
import com.blog.mapper.UserMapper;
import com.blog.service.GrowthEventService;
import com.blog.service.UserService;
import com.blog.util.PasswordUtil;
import com.blog.vo.LoginVO;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final GrowthEventService growthEventService;

    public UserServiceImpl(GrowthEventService growthEventService) {
        this.growthEventService = growthEventService;
    }

    @Override
    public LoginVO loginAdmin(LoginDTO dto) {
        return loginByRoles(dto, UserRole.ADMIN);
    }

    @Override
    public LoginVO loginUser(LoginDTO dto) {
        return loginByRoles(dto, UserRole.USER, UserRole.ADMIN);
    }

    @Override
    public LoginVO registerAdmin(RegisterDTO dto) {
        return registerByRole(dto, UserRole.ADMIN);
    }

    @Override
    public LoginVO registerUser(RegisterDTO dto) {
        return registerByRole(dto, UserRole.USER);
    }

    @Override
    public User getByToken(String token) {
        return null;
    }

    private LoginVO loginByRoles(LoginDTO dto, UserRole... allowedRoles) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Set<String> allowedRoleNames = Arrays.stream(allowedRoles)
                .map(Enum::name)
                .collect(Collectors.toSet());
        if (!allowedRoleNames.contains(user.getRole())) {
            throw new AppException(ErrorCode.ROLE_FORBIDDEN);
        }

        String storedPassword = user.getPassword();
        boolean valid;

        if (PasswordUtil.isBCrypt(storedPassword)) {
            valid = PasswordUtil.check(dto.getPassword(), storedPassword);
        } else {
            valid = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()).equals(storedPassword);
            if (valid) {
                user.setPassword(PasswordUtil.hash(dto.getPassword()));
                updateById(user);
            }
        }

        if (!valid) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        StpUtil.login(user.getId());
        growthEventService.record("user_logged_in", user.getId(), null, "{\"role\":\"" + user.getRole() + "\"}");

        return buildLoginVO(user);
    }

    private LoginVO registerByRole(RegisterDTO dto, UserRole role) {
        User exists = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (exists != null) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (StringUtils.hasText(dto.getEmail())) {
            User emailExists = getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, dto.getEmail()));
            if (emailExists != null) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(StringUtils.hasText(dto.getEmail()) ? dto.getEmail() : dto.getUsername() + "@example.com");
        user.setPassword(PasswordUtil.hash(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setAvatar(dto.getAvatar());
        user.setRole(role.name());
        save(user);

        StpUtil.login(user.getId());
        growthEventService.record("user_registered", user.getId(), null, "{\"role\":\"" + role.name() + "\"}");
        return buildLoginVO(user);
    }

    private LoginVO buildLoginVO(User user) {
        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserInfo(LoginVO.UserInfo.fromUser(user));
        return vo;
    }
}
