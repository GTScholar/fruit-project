package com.fruitfresh.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.dto.ProductSaveDTO;
import com.fruitfresh.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {

    /** 用户端：商品分页查询（只看上架商品） */
    Page<Product> page(int pageNum, int pageSize, Long categoryId, String keyword);

    /** 用户端：热销商品（Redis 缓存 10 分钟） */
    List<Product> hotList();

    /** 用户端：商品详情（Redis 缓存 10 分钟） */
    Product detail(Long id);

    /** 管理端：商品分页查询（含下架商品） */
    Page<Product> adminPage(int pageNum, int pageSize, Long categoryId, String keyword, Integer status);

    /** 管理端：新增商品 */
    void save(ProductSaveDTO dto);

    /** 管理端：修改商品 */
    void update(ProductSaveDTO dto);

    /** 管理端：删除商品（逻辑删除） */
    void delete(Long id);

    /** 管理端：上架/下架 */
    void toggleStatus(Long id, Integer status);

    /** 管理端：设置/取消热销 */
    void toggleHot(Long id, Integer hot);

    /** 清除热销缓存 */
    void evictHotCache();

    /** 清除单个商品相关缓存 */
    void evictProductCache(Long productId);
}
