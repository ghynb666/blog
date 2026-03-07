package com.blog.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.TagService;
import com.blog.vo.ArchiveVO;
import com.blog.vo.ArticleDetailVO;
import com.blog.vo.ArticleListVO;
import com.blog.vo.CategoryVO;
import com.blog.vo.TagVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController("frontArticleController")
@RequestMapping({"/api", "/api/v1"})
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    @GetMapping("/articles")
    public Result<Page<ArticleListVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) List<Long> tagIds) {
        return Result.success(articleService.frontList(page, size, keyword, orderBy, categoryId, tagIds));
    }

    @GetMapping("/articles/{id}")
    public Result<ArticleDetailVO> detail(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(articleService.frontDetail(id, request));
    }

    @GetMapping("/categories")
    public Result<List<CategoryVO>> categories() {
        return Result.success(categoryService.list());
    }

    @GetMapping("/tags")
    public Result<List<TagVO>> tags() {
        return Result.success(tagService.list());
    }

    @GetMapping("/archives")
    public Result<List<ArchiveVO>> archives() {
        return Result.success(articleService.archives());
    }

    @GetMapping("/articles/category/{categoryId}")
    public Result<Page<ArticleListVO>> listByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) List<Long> tagIds) {
        return Result.success(articleService.frontList(page, size, null, null, categoryId, tagIds));
    }

    @GetMapping("/articles/tag/{tagId}")
    public Result<Page<ArticleListVO>> listByTag(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(articleService.frontList(page, size, null, null, categoryId, List.of(tagId)));
    }

    @GetMapping("/articles/tags")
    public Result<Page<ArticleListVO>> listByTags(
            @RequestParam List<Long> tagIds,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(articleService.frontList(page, size, null, null, categoryId, tagIds));
    }
}
