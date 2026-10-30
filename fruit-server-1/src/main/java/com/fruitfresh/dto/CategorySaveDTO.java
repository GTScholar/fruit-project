package com.fruitfresh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增 / 修改商品分类请求（管理端）
 */
@Data
public class CategorySaveDTO {

    /** 分类ID：新增时忽略，修改时必填 */
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 20, message = "分类名称最长20个字符")
    private String name;

    /** 分类图标 */
    private String icon;

    /** 排序值，越小越靠前，不传默认0 */
    private Integer sort;

    /** 状态 1启用 0停用，不传默认启用 */
    private Integer status;
}
