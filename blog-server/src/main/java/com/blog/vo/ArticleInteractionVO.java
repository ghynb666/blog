package com.blog.vo;

import lombok.Data;

@Data
public class ArticleInteractionVO {

    private Long articleId;

    private Long commentCount;

    private Long likeCount;

    private Boolean liked;
}
