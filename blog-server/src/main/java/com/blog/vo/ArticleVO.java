package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleVO {

    private Long id;

    private String title;

    private String summary;

    private String content;

    private String cover;

    private Integer status;

    private Long viewCount;

    private Long categoryId;

    private String categoryName;

    private List<TagVO> tags;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
