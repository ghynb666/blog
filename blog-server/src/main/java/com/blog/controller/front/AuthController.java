package com.blog.controller.front;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.util.RsaUtil;
import com.blog.vo.LoginVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("frontAuthController")
@RequestMapping({"/api", "/api/v1"})
public class AuthController {

    private final UserService userService;
    private final RsaUtil rsaUtil;

    public AuthController(UserService userService, RsaUtil rsaUtil) {
        this.userService = userService;
        this.rsaUtil = rsaUtil;
    }

    @PostMapping("/login")
    public Result<LoginVO> loginUser(@RequestBody LoginDTO dto) {
        String password = rsaUtil.decrypt(dto.getPassword());
        dto.setPassword(password);
        return Result.success(userService.loginUser(dto));
    }

    @PostMapping("/register")
    public Result<LoginVO> registerUser(@Valid @RequestBody RegisterDTO dto) {
        String password = rsaUtil.decrypt(dto.getPassword());
        dto.setPassword(password);
        return Result.success(userService.registerUser(dto));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }

    @GetMapping("/me")
    public Result<LoginVO.UserInfo> me() {
        StpUtil.checkLogin();
        User user = userService.getById(StpUtil.getLoginIdAsLong());
        return Result.success(LoginVO.UserInfo.fromUser(user));
    }
}
