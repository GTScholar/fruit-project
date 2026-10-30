package com.fruitfresh.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.common.BusinessException;
import com.fruitfresh.dto.BannerSaveDTO;
import com.fruitfresh.entity.Banner;
import com.fruitfresh.enums.YesNo;
import com.fruitfresh.mapper.BannerMapper;
import com.fruitfresh.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 轮播图服务实现
 */
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /** 轮播图缓存 key */
    private static final String CACHE_KEY = "fruit:banner:list";

    /** 用户端：查询启用中的轮播图（Redis 缓存 30 分钟） */
    @Override
    public List<Banner> list() {
        Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            @SuppressWarnings("unchecked")
            List<Banner> list = (List<Banner>) cached;
            return list;
        }
        List<Banner> list = bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, YesNo.YES.getCode())
                .orderByAsc(Banner::getSort));
        redisTemplate.opsForValue().set(CACHE_KEY, list, 30, TimeUnit.MINUTES);
        return list;
    }

    /** 管理端：分页查询 */
    @Override
    public Page<Banner> adminPage(int pageNum, int pageSize) {
        Page<Banner> page = new Page<>(pageNum, pageSize);
        return bannerMapper.selectPage(page, new LambdaQueryWrapper<Banner>()
                .orderByAsc(Banner::getSort));
    }

    /** 新增 */
    @Override
    public void save(BannerSaveDTO dto) {
        Banner banner = new Banner();
        BeanUtil.copyProperties(dto, banner, "id");
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(YesNo.YES.getCode());
        }
        bannerMapper.insert(banner);
        evictCache();
    }

    /** 修改 */
    @Override
    public void update(BannerSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("轮播图ID不能为空");
        }
        Banner banner = new Banner();
        BeanUtil.copyProperties(dto, banner);
        bannerMapper.updateById(banner);
        evictCache();
    }

    /** 删除 */
    @Override
    public void delete(Long id) {
        bannerMapper.deleteById(id);
        evictCache();
    }

    /** 启用/停用 */
    @Override
    public void toggleStatus(Long id, Integer status) {
        if (YesNo.of(status) == null) {
            throw new BusinessException("状态值不正确");
        }
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(status);
        bannerMapper.updateById(banner);
        evictCache();
    }

    /** 清除缓存 */
    @Override
    public void evictCache() {
        redisTemplate.delete(CACHE_KEY);
    }
}
