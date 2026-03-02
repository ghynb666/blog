package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.dto.LoginDTO;
import com.blog.entity.User;
import com.blog.vo.LoginVO;

public interface UserService extends IService<User> {

    LoginVO login(LoginDTO dto);
    User getByToken(String token);
}
