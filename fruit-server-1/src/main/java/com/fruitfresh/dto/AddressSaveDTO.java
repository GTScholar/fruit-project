package com.fruitfresh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增 / 修改收货地址请求
 */
@Data
public class AddressSaveDTO {

    /** 地址ID：新增时忽略，修改时必填 */
    private Long id;

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 20, message = "收货人姓名最长20个字符")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 100, message = "详细地址最长100个字符")
    private String detail;

    /** 是否默认 1是 0否，不传默认否 */
    private Integer isDefault;
}
