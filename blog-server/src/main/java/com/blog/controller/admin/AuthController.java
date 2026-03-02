package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.dto.LoginDTO;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @GetMapping("/info")
    public Result<User> info(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        return Result.success(userService.getByToken(token));
    }
}
