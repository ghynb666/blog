package com.blog.service;

import com.blog.dto.CategoryDTO;
import com.blog.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    void create(CategoryDTO dto);

    void update(Long id, CategoryDTO dto);

    void delete(Long id);

    List<CategoryVO> list();

    CategoryVO detail(Long id);
}
