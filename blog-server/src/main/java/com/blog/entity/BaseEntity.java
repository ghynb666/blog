package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("created_at")
    private LocalDateTime createTime;

    @TableField("updated_at")
    private LocalDateTime updateTime;
}
