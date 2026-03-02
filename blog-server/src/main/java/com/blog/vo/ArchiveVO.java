package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArchiveVO {

    private Integer year;

    private Integer month;

    private List<ArticleSimpleVO> articles;

    @Data
    public static class ArticleSimpleVO {

        private Long id;

        private String title;

        private String summary;

        private LocalDateTime createdAt;
    }
}
