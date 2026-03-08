package com.blog.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SubscriptionCreateDTO {

    @NotBlank(message = "cannot be blank")
    @Email(message = "must be a valid email")
    @Size(max = 100, message = "length must be <= 100")
    private String email;

    @Size(max = 255, message = "length must be <= 255")
    private String sourcePage;
}
