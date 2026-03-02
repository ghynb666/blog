package com.blog.controller.admin;

import com.blog.dto.LoginDTO;
import com.blog.service.UserService;
import com.blog.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginVO login(@RequestBody LoginDTO dto) {
        return userService.login(dto);
    }
}
