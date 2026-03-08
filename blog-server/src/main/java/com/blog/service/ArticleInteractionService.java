package com.blog.service;

import com.blog.dto.CommentCreateDTO;
import com.blog.vo.ArticleCommentVO;
import com.blog.vo.ArticleInteractionVO;

import java.util.List;

public interface ArticleInteractionService {

    List<ArticleCommentVO> listComments(Long articleId);

    ArticleCommentVO createComment(Long articleId, Long userId, CommentCreateDTO dto);

    ArticleInteractionVO toggleLike(Long articleId, Long userId);

    ArticleInteractionVO getInteraction(Long articleId, Long userId);
}
