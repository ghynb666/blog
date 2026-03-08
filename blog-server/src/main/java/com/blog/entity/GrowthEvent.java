package com.blog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("growth_event")
public class GrowthEvent extends BaseEntity {

    private String eventType;

    private Long userId;

    private Long articleId;

    private String eventData;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
