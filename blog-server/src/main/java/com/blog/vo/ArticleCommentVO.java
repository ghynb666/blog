package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleCommentVO {

    private Long id;

    private Long articleId;

    private String content;

    private Integer status;

    private LocalDateTime createdAt;

    private UserSummary user;

    @Data
    public static class UserSummary {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
    }
}
