package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleListVO {

    private Long id;

    private String title;

    private String summary;

    private String cover;

    private Integer status;

    private Long viewCount;

    private Long categoryId;

    private String categoryName;

    private LocalDateTime createTime;
}
