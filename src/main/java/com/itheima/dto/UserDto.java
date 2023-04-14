package com.itheima.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dsapa
 * @date 2023/4/8 20:43
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer code; //0 调用成功 -1或其他值 调用失败并回复message

    private String message; //对本次接口调用的信息描述

    private DataInfo data; //返回数据

}
