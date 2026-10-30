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
import java.time.LocalDateTime;

/**
 * 商品分类实体
 */
@Data
@TableName("t_category")
public class Category implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String name;

    /** 分类图标（emoji 或图片） */
    private String icon;

    /** 排序值，越小越靠前 */
    private Integer sort;

    /** 状态 1启用 0停用 */
    private Integer status;

    @TableLogic
    @JsonIgnore
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
