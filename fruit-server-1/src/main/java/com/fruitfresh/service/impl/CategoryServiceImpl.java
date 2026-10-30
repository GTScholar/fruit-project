package com.fruitfresh.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.BusinessException;
import com.fruitfresh.dto.CategorySaveDTO;
import com.fruitfresh.entity.Category;
import com.fruitfresh.enums.YesNo;
import com.fruitfresh.mapper.CategoryMapper;
import com.fruitfresh.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 商品分类服务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /** 分类列表缓存 key */
    private static final String CACHE_KEY = "fruit:category:list";

    /** 用户端：查询启用的分类列表（Redis 缓存 30 分钟） */
    @Override
    public List<Category> list() {
        Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            @SuppressWarnings("unchecked")
            List<Category> list = (List<Category>) cached;
            return list;
        }
        List<Category> list = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, YesNo.YES.getCode())
                .orderByAsc(Category::getSort));
        redisTemplate.opsForValue().set(CACHE_KEY, list, 30, TimeUnit.MINUTES);
        return list;
    }

    /** 管理端：分页查询（含停用分类） */
    @Override
    public Page<Category> adminPage(int pageNum, int pageSize, String name) {
        Page<Category> page = new Page<>(pageNum, pageSize);
        return categoryMapper.selectPage(page, new LambdaQueryWrapper<Category>()
                .like(StrUtil.isNotBlank(name), Category::getName, name)
                .orderByAsc(Category::getSort));
    }

    /** 新增分类 */
    @Override
    public void save(CategorySaveDTO dto) {
        Category category = new Category();
        BeanUtil.copyProperties(dto, category, "id");
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(YesNo.YES.getCode());
        }
        categoryMapper.insert(category);
        evictCache();
    }

    /** 修改分类 */
    @Override
    public void update(CategorySaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        Category category = new Category();
        BeanUtil.copyProperties(dto, category);
        categoryMapper.updateById(category);
        evictCache();
    }

    /** 删除分类（逻辑删除） */
    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
        evictCache();
    }

    /** 启用/停用 */
    @Override
    public void toggleStatus(Long id, Integer status) {
        if (YesNo.of(status) == null) {
            throw new BusinessException("状态值不正确");
        }
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);
        categoryMapper.updateById(category);
        evictCache();
    }

    /** 清除分类缓存 */
    @Override
    public void evictCache() {
        redisTemplate.delete(CACHE_KEY);
    }
}
