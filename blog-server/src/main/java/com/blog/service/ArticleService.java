package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.ArticleDTO;
import com.blog.vo.ArchiveVO;
import com.blog.vo.ArticleDetailVO;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ArticleService {

    void create(ArticleDTO dto);

    void update(Long id, ArticleDTO dto);

    void delete(Long id);

    Page<ArticleListVO> list(Integer page, Integer size, Integer status, Long categoryId);

    ArticleVO detail(Long id);

    Page<ArticleListVO> frontList(Integer page, Integer size, String keyword, String orderBy);

    ArticleDetailVO frontDetail(Long id, HttpServletRequest request);

    List<ArchiveVO> archives();

    Page<ArticleListVO> listByCategory(Long categoryId, Integer page, Integer size);

    Page<ArticleListVO> listByTag(Long tagId, Integer page, Integer size);

    Page<ArticleListVO> listByTags(List<Long> tagIds, Integer page, Integer size);

    void incrementViewCount(Long id);
}
