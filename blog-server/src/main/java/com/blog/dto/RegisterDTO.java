package com.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterDTO {

    @NotBlank(message = "cannot be blank")
    @Size(max = 50, message = "length must be <= 50")
    private String username;

    @NotBlank(message = "cannot be blank")
    @Size(min = 6, max = 100, message = "length must be between 6 and 100")
    private String password;

    @Size(max = 50, message = "length must be <= 50")
    private String nickname;

    @Size(max = 255, message = "length must be <= 255")
    private String avatar;
}

