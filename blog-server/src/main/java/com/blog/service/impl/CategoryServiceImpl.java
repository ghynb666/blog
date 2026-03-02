package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.dto.CategoryDTO;
import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import com.blog.service.CategoryService;
import com.blog.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public void create(CategoryDTO dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        if (category.getSort() == null) {
            category.setSort(0);
        }
        categoryMapper.insert(category);
    }

    @Override
    public void update(Long id, CategoryDTO dto) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        BeanUtils.copyProperties(dto, category);
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public List<CategoryVO> list() {
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        return categories.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public CategoryVO detail(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return toVO(category);
    }

    private CategoryVO toVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
