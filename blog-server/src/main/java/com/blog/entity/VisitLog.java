package com.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("visit_log")
public class VisitLog extends BaseEntity {

    private Long articleId;

    private String ip;

    private String userAgent;
}
