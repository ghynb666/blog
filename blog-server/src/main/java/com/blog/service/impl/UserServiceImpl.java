package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dto.LoginDTO;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import com.blog.util.JwtUtil;
import com.blog.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String md5Password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        if (!md5Password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserInfo(LoginVO.UserInfo.fromUser(user));
        return vo;
    }
}
