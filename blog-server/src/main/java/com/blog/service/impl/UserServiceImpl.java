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
import com.blog.service.UserService;
import com.blog.util.PasswordUtil;
import com.blog.vo.LoginVO;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public LoginVO loginAdmin(LoginDTO dto) {
        return loginByRole(dto, UserRole.ADMIN);
    }

    @Override
    public LoginVO loginUser(LoginDTO dto) {
        return loginByRole(dto, UserRole.USER);
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

    private LoginVO loginByRole(LoginDTO dto, UserRole expectedRole) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (!expectedRole.name().equalsIgnoreCase(user.getRole())) {
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

        return buildLoginVO(user);
    }

    private LoginVO registerByRole(RegisterDTO dto, UserRole role) {
        User exists = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (exists != null) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.hash(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setAvatar(dto.getAvatar());
        user.setRole(role.name());
        save(user);

        StpUtil.login(user.getId());
        return buildLoginVO(user);
    }

    private LoginVO buildLoginVO(User user) {
        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserInfo(LoginVO.UserInfo.fromUser(user));
        return vo;
    }
}
