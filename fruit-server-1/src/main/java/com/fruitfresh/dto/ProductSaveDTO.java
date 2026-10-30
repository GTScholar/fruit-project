package com.fruitfresh.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增 / 修改商品请求（管理端）
 * <p>
 * 只暴露允许前端写入的字段：sales、deleted 等由服务端维护。
 */
@Data
public class ProductSaveDTO {

    /** 商品ID：新增时忽略，修改时必填 */
    private Long id;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 50, message = "商品名称最长50个字符")
    private String name;

    @NotNull(message = "请选择商品分类")
    private Long categoryId;

    /** 商品主图 */
    private String image;

    /** 商品描述 */
    private String description;

    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originalPrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;

    /** 是否热销 1是 0否，不传默认否 */
    private Integer hot;

    /** 状态 1上架 0下架，不传默认上架 */
    private Integer status;
}
