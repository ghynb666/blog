package com.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseEntity {

    private String title;

    private String summary;

    private String content;

    private String cover;

    private Integer status;

    private Long viewCount;

    private Long categoryId;
}
