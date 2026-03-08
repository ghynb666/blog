package com.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentCreateDTO {

    private Long parentId;

    @NotBlank(message = "cannot be blank")
    @Size(max = 500, message = "length must be <= 500")
    private String content;
}
