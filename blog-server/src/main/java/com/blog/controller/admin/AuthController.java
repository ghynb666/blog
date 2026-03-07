package com.blog.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.dto.LoginDTO;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.util.RsaUtil;
import com.blog.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/admin", "/api/v1/admin"})
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RsaUtil rsaUtil;

    @GetMapping("/public-key")
    public Result<Map<String, String>> getPublicKey() {
        return Result.success(Map.of("publicKey", rsaUtil.getPublicKey()));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        String password = rsaUtil.decrypt(dto.getPassword());
        dto.setPassword(password);
        return Result.success(userService.login(dto));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }

    @GetMapping("/info")
    public Result<User> info() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getById(userId));
    }
}
