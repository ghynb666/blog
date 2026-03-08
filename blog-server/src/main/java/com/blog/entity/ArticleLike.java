package com.blog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_like")
public class ArticleLike extends BaseEntity {

    private Long articleId;

    private Long userId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
