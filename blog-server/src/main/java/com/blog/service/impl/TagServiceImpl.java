package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.cache.BypassCacheEvict;
import com.blog.cache.BypassCacheable;
import com.blog.common.AppException;
import com.blog.common.ErrorCode;
import com.blog.dto.TagDTO;
import com.blog.entity.Tag;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import com.blog.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Override
    @BypassCacheEvict(
            keys = {
                    "T(com.blog.cache.CacheKeys).tagList()"
            },
            patterns = {
                    "T(com.blog.cache.CacheKeys).adminArticleListPattern()",
                    "T(com.blog.cache.CacheKeys).adminArticleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).articleListPattern()",
                    "T(com.blog.cache.CacheKeys).articleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).feedSitemapPattern()"
            }
    )
    public void create(TagDTO dto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(dto, tag);
        tagMapper.insert(tag);
    }

    @Override
    @BypassCacheEvict(
            keys = {
                    "T(com.blog.cache.CacheKeys).tagList()"
            },
            patterns = {
                    "T(com.blog.cache.CacheKeys).adminArticleListPattern()",
                    "T(com.blog.cache.CacheKeys).adminArticleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).articleListPattern()",
                    "T(com.blog.cache.CacheKeys).articleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).feedSitemapPattern()"
            }
    )
    public void update(Long id, TagDTO dto) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new AppException(ErrorCode.TAG_NOT_FOUND);
        }
        BeanUtils.copyProperties(dto, tag);
        tagMapper.updateById(tag);
    }

    @Override
    @BypassCacheEvict(
            keys = {
                    "T(com.blog.cache.CacheKeys).tagList()"
            },
            patterns = {
                    "T(com.blog.cache.CacheKeys).adminArticleListPattern()",
                    "T(com.blog.cache.CacheKeys).adminArticleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).articleListPattern()",
                    "T(com.blog.cache.CacheKeys).articleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).feedSitemapPattern()"
            }
    )
    public void delete(Long id) {
        tagMapper.deleteById(id);
    }

    @Override
    @BypassCacheable(
            key = "T(com.blog.cache.CacheKeys).tagList()",
            ttlSeconds = 1800
    )
    public List<TagVO> list() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<Tag>().orderByDesc(Tag::getCreatedAt));
        return tags.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public TagVO detail(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new AppException(ErrorCode.TAG_NOT_FOUND);
        }
        return toVO(tag);
    }

    private TagVO toVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}
