package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.dto.TagDTO;
import com.blog.service.TagService;
import com.blog.vo.TagVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping({"/api/admin/tags", "/api/v1/admin/tags"})
public class TagController {

    @Resource
    private TagService tagService;

    @PostMapping
    public Result<Void> create(@Valid @RequestBody TagDTO dto) {
        tagService.create(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TagDTO dto) {
        tagService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }

    @GetMapping
    public Result<List<TagVO>> list() {
        return Result.success(tagService.list());
    }

    @GetMapping("/{id}")
    public Result<TagVO> detail(@PathVariable Long id) {
        return Result.success(tagService.detail(id));
    }
}
