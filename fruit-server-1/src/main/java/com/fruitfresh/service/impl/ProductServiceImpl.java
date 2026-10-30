package com.fruitfresh.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.BusinessException;
import com.fruitfresh.dto.ProductSaveDTO;
import com.fruitfresh.entity.Product;
import com.fruitfresh.enums.ProductStatus;
import com.fruitfresh.enums.YesNo;
import com.fruitfresh.mapper.ProductMapper;
import com.fruitfresh.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /** 热销商品缓存 key */
    private static final String HOT_KEY = "fruit:hot:products";
    /** 商品详情缓存 key 前缀 */
    private static final String DETAIL_KEY = "fruit:product:";

    /** 用户端：商品分页查询（只看上架商品） */
    @Override
    public Page<Product> page(int pageNum, int pageSize, Long categoryId, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        return productMapper.selectPage(page, new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, ProductStatus.ON_SHELF.getCode())
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .like(StrUtil.isNotBlank(keyword), Product::getName, keyword)
                .orderByDesc(Product::getSales));
    }

    /** 用户端：热销商品（Redis 缓存 10 分钟） */
    @Override
    public List<Product> hotList() {
        Object cached = redisTemplate.opsForValue().get(HOT_KEY);
        if (cached != null) {
            @SuppressWarnings("unchecked")
            List<Product> list = (List<Product>) cached;
            return list;
        }
        List<Product> list = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, ProductStatus.ON_SHELF.getCode())
                .eq(Product::getHot, YesNo.YES.getCode())
                .orderByDesc(Product::getSales)
                .last("LIMIT 8"));
        redisTemplate.opsForValue().set(HOT_KEY, list, 10, TimeUnit.MINUTES);
        return list;
    }

    /** 用户端：商品详情（Redis 缓存 10 分钟） */
    @Override
    public Product detail(Long id) {
        String key = DETAIL_KEY + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return (Product) cached;
        }
        Product product = productMapper.selectById(id);
        if (product == null || !ProductStatus.ON_SHELF.is(product.getStatus())) {
            throw new BusinessException("商品不存在或已下架");
        }
        redisTemplate.opsForValue().set(key, product, 10, TimeUnit.MINUTES);
        return product;
    }

    /** 管理端：商品分页查询（含下架商品） */
    @Override
    public Page<Product> adminPage(int pageNum, int pageSize, Long categoryId, String keyword, Integer status) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        return productMapper.selectPage(page, new LambdaQueryWrapper<Product>()
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .like(StrUtil.isNotBlank(keyword), Product::getName, keyword)
                .eq(status != null, Product::getStatus, status)
                .orderByDesc(Product::getCreateTime));
    }

    /** 管理端：新增商品 */
    @Override
    public void save(ProductSaveDTO dto) {
        Product product = new Product();
        // 新增不允许由前端指定主键
        BeanUtil.copyProperties(dto, product, "id");
        if (product.getSales() == null) {
            product.setSales(0);
        }
        if (product.getHot() == null) {
            product.setHot(YesNo.NO.getCode());
        }
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.ON_SHELF.getCode());
        }
        productMapper.insert(product);
        evictHotCache();
    }

    /** 管理端：修改商品 */
    @Override
    public void update(ProductSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        Product product = new Product();
        BeanUtil.copyProperties(dto, product);
        productMapper.updateById(product);
        evictProductCache(dto.getId());
    }

    /** 管理端：删除商品（逻辑删除） */
    @Override
    public void delete(Long id) {
        productMapper.deleteById(id);
        evictProductCache(id);
    }

    /** 管理端：上架/下架 */
    @Override
    public void toggleStatus(Long id, Integer status) {
        if (ProductStatus.of(status) == null) {
            throw new BusinessException("状态值不正确");
        }
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        productMapper.updateById(product);
        evictProductCache(id);
    }

    /** 管理端：设置/取消热销 */
    @Override
    public void toggleHot(Long id, Integer hot) {
        if (YesNo.of(hot) == null) {
            throw new BusinessException("热销标记值不正确");
        }
        Product product = new Product();
        product.setId(id);
        product.setHot(hot);
        productMapper.updateById(product);
        evictProductCache(id);
    }

    /** 清除热销缓存 */
    @Override
    public void evictHotCache() {
        redisTemplate.delete(HOT_KEY);
    }

    /** 清除单个商品相关缓存 */
    @Override
    public void evictProductCache(Long productId) {
        redisTemplate.delete(HOT_KEY);
        redisTemplate.delete(DETAIL_KEY + productId);
    }
}
