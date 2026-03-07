package com.blog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.dto.ArticleDTO;
import com.blog.service.ArticleService;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController("adminArticleController")
@RequestMapping({"/api/admin/articles", "/api/v1/admin/articles"})
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ArticleDTO dto) {
        articleService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ArticleDTO dto) {
        articleService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }

    @GetMapping
    public Result<Page<ArticleListVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(articleService.list(page, size, status, categoryId));
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        return Result.success(articleService.detail(id));
    }
}
