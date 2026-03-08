package com.blog.controller.front;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.dto.CommentCreateDTO;
import com.blog.service.ArticleInteractionService;
import com.blog.vo.ArticleCommentVO;
import com.blog.vo.ArticleInteractionVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping({"/api", "/api/v1"})
public class InteractionController {

    private final ArticleInteractionService articleInteractionService;

    public InteractionController(ArticleInteractionService articleInteractionService) {
        this.articleInteractionService = articleInteractionService;
    }

    @GetMapping("/articles/{id}/comments")
    public Result<List<ArticleCommentVO>> listComments(@PathVariable Long id) {
        return Result.success(articleInteractionService.listComments(id));
    }

    @PostMapping("/articles/{id}/comments")
    public Result<ArticleCommentVO> createComment(@PathVariable Long id, @Valid @RequestBody CommentCreateDTO dto) {
        StpUtil.checkLogin();
        return Result.success(articleInteractionService.createComment(id, StpUtil.getLoginIdAsLong(), dto));
    }

    @PostMapping("/articles/{id}/like")
    public Result<ArticleInteractionVO> toggleLike(@PathVariable Long id) {
        StpUtil.checkLogin();
        return Result.success(articleInteractionService.toggleLike(id, StpUtil.getLoginIdAsLong()));
    }
}
