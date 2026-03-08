package com.blog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("subscription")
public class Subscription extends BaseEntity {

    private String email;

    private String sourcePage;

    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
