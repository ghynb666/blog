package com.blog.service;

import com.blog.dto.LoginDTO;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.vo.LoginVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.blog.service.impl.UserServiceImpl;
import com.blog.util.JwtUtil;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        testUser.setNickname("管理员");

        loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("123456");
    }

    @Test
    void login_Success() {
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("test-token");

        LoginVO result = userService.login(loginDTO);

        assertNotNull(result);
        assertEquals("test-token", result.getToken());
        assertNotNull(result.getUserInfo());
        assertEquals("admin", result.getUserInfo().getUsername());

        verify(userMapper).selectOne(any());
        verify(jwtUtil).generateToken(1L, "admin");
    }

    @Test
    void login_UserNotFound() {
        when(userMapper.selectOne(any())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(loginDTO);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void login_WrongPassword() {
        loginDTO.setPassword("wrongpassword");
        when(userMapper.selectOne(any())).thenReturn(testUser);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(loginDTO);
        });

        assertEquals("密码错误", exception.getMessage());
    }
}
