package com.blog.service;

import com.blog.dto.TagDTO;
import com.blog.vo.TagVO;

import java.util.List;

public interface TagService {

    void create(TagDTO dto);

    void update(Long id, TagDTO dto);

    void delete(Long id);

    List<TagVO> list();

    TagVO detail(Long id);
}
