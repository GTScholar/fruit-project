package com.fruitfresh.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.dto.CategorySaveDTO;
import com.fruitfresh.entity.Category;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface CategoryService {

    /** 用户端：查询启用的分类列表（Redis 缓存 30 分钟） */
    List<Category> list();

    /** 管理端：分页查询（含停用分类） */
    Page<Category> adminPage(int pageNum, int pageSize, String name);

    /** 新增分类 */
    void save(CategorySaveDTO dto);

    /** 修改分类 */
    void update(CategorySaveDTO dto);

    /** 删除分类（逻辑删除） */
    void delete(Long id);

    /** 启用/停用 */
    void toggleStatus(Long id, Integer status);

    /** 清除分类缓存 */
    void evictCache();
}
