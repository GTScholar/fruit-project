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
 * 轮播图实体
 */
@Data
@TableName("t_banner")
public class Banner implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 轮播图地址 */
    private String image;

    /** 标题 */
    private String title;

    /** 跳转链接 */
    private String linkUrl;

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
