package com.fruitfresh.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fruitfresh.dto.BannerSaveDTO;
import com.fruitfresh.entity.Banner;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface BannerService {

    /** 用户端：查询启用中的轮播图（Redis 缓存 30 分钟） */
    List<Banner> list();

    /** 管理端：分页查询 */
    Page<Banner> adminPage(int pageNum, int pageSize);

    /** 新增 */
    void save(BannerSaveDTO dto);

    /** 修改 */
    void update(BannerSaveDTO dto);

    /** 删除 */
    void delete(Long id);

    /** 启用/停用 */
    void toggleStatus(Long id, Integer status);

    /** 清除缓存 */
    void evictCache();
}
