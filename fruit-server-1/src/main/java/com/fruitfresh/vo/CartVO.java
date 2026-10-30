package com.fruitfresh.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车视图对象（购物车 + 商品信息）
 */
@Data
public class CartVO implements Serializable {

    /** 购物车记录ID */
    private Long id;

    /** 商品ID */
    private Long productId;

    /** 商品名称 */
    private String name;

    /** 商品图片 */
    private String image;

    /** 售价 */
    private BigDecimal price;

    /** 数量 */
    private Integer quantity;

    /** 是否勾选 1是 0否 */
    private Integer checked;

    /** 库存 */
    private Integer stock;

    /** 商品状态 1上架 0下架 */
    private Integer status;
}
