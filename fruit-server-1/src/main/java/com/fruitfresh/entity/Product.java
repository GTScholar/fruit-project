package com.fruitfresh.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("t_product")
public class Product implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品名称 */
    private String name;

    /** 分类ID */
    private Long categoryId;

    /** 商品主图 */
    private String image;

    /** 商品描述 */
    private String description;

    /** 售价 */
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 库存 */
    private Integer stock;

    /** 销量 */
    private Integer sales;

    /** 是否热销 1是 0否 */
    private Integer hot;

    /** 状态 1上架 0下架 */
    private Integer status;

    @TableLogic
    @JsonIgnore
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
