package com.blog.vo;

import lombok.Data;

@Data
public class VisitStatVO {

    private Long articleId;

    private Integer viewCount;

    private Integer todayVisits;

    private Integer totalVisits;
}
