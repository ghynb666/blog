package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.entity.User;
import com.blog.vo.LoginVO;

public interface UserService extends IService<User> {

    LoginVO loginAdmin(LoginDTO dto);

    LoginVO loginUser(LoginDTO dto);

    LoginVO registerAdmin(RegisterDTO dto);

    LoginVO registerUser(RegisterDTO dto);

    User getByToken(String token);
}
