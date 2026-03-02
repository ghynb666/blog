package com.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.impl.ArticleServiceImpl;
import com.blog.service.VisitLogService;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private ArticleTagMapper articleTagMapper;

    @Mock
    private VisitLogService visitLogService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private Article testArticle;
    private ArticleDTO articleDTO;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("技术");

        testArticle = new Article();
        testArticle.setId(1L);
        testArticle.setTitle("测试文章");
        testArticle.setContent("测试内容");
        testArticle.setSummary("测试摘要");
        testArticle.setStatus(1);
        testArticle.setViewCount(0);
        testArticle.setCategoryId(1L);

        articleDTO = new ArticleDTO();
        articleDTO.setTitle("新文章");
        articleDTO.setContent("新内容");
        articleDTO.setSummary("新摘要");
        articleDTO.setStatus(1);
        articleDTO.setCategoryId(1L);
        articleDTO.setTagIds(Arrays.asList(1L, 2L));
    }

    @Test
    void create_Success() {
        when(articleMapper.insert(any(Article.class))).thenReturn(1);
        when(articleTagMapper.insert(any(ArticleTag.class))).thenReturn(1);

        articleService.create(articleDTO);

        verify(articleMapper).insert(any(Article.class));
        verify(articleTagMapper, times(2)).insert(any(ArticleTag.class));
    }

    @Test
    void update_Success() {
        when(articleMapper.selectById(1L)).thenReturn(testArticle);
        when(articleMapper.updateById(any(Article.class))).thenReturn(1);
        when(articleTagMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);
        when(articleTagMapper.insert(any(ArticleTag.class))).thenReturn(1);

        articleService.update(1L, articleDTO);

        verify(articleMapper).selectById(1L);
        verify(articleMapper).updateById(any(Article.class));
        verify(articleTagMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    void update_ArticleNotFound() {
        when(articleMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            articleService.update(1L, articleDTO);
        });

        assertEquals("文章不存在", exception.getMessage());
    }

    @Test
    void delete_Success() {
        when(articleMapper.deleteById(1L)).thenReturn(1);
        when(articleTagMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);

        articleService.delete(1L);

        verify(articleMapper).deleteById(1L);
        verify(articleTagMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    void detail_Success() {
        when(articleMapper.selectById(1L)).thenReturn(testArticle);
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);
        when(articleTagMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());

        ArticleVO result = articleService.detail(1L);

        assertNotNull(result);
        assertEquals("测试文章", result.getTitle());
        assertEquals("技术", result.getCategoryName());
    }

    @Test
    void detail_ArticleNotFound() {
        when(articleMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            articleService.detail(1L);
        });

        assertEquals("文章不存在", exception.getMessage());
    }

    @Test
    void list_Success() {
        Page<Article> articlePage = new Page<>(1, 10);
        articlePage.setRecords(Arrays.asList(testArticle));
        articlePage.setTotal(1);

        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(articlePage);
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);

        Page<ArticleListVO> result = articleService.list(1, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("测试文章", result.getRecords().get(0).getTitle());
    }

    @Test
    void list_WithFilters() {
        Page<Article> articlePage = new Page<>(1, 10);
        articlePage.setRecords(Arrays.asList(testArticle));
        articlePage.setTotal(1);

        when(articleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(articlePage);
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);

        Page<ArticleListVO> result = articleService.list(1, 10, 1, 1L);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }
}
