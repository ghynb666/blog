package com.blog.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.common.AppException;
import com.blog.common.ErrorCode;
import com.blog.dto.LoginDTO;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import com.blog.util.PasswordUtil;
import com.blog.vo.LoginVO;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
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

        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserInfo(LoginVO.UserInfo.fromUser(user));
        return vo;
    }

    @Override
    public User getByToken(String token) {
        return null;
    }
}
