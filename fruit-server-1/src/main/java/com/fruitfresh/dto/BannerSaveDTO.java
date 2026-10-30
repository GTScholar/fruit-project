package com.fruitfresh.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增 / 修改轮播图请求（管理端）
 */
@Data
public class BannerSaveDTO {

    /** 轮播图ID：新增时忽略，修改时必填 */
    private Long id;

    @NotBlank(message = "轮播图地址不能为空")
    private String image;

    /** 标题 */
    private String title;

    /** 跳转链接 */
    private String linkUrl;

    /** 排序值，越小越靠前，不传默认0 */
    private Integer sort;

    /** 状态 1启用 0停用，不传默认启用 */
    private Integer status;
}
