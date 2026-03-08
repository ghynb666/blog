package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ArticleCommentVO {

    private Long id;

    private Long articleId;

    private Long parentId;

    private String content;

    private Integer status;

    private LocalDateTime createdAt;

    private UserSummary user;

    private UserSummary replyToUser;

    private List<ArticleCommentVO> children = new ArrayList<>();

    @Data
    public static class UserSummary {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
    }
}
