package com.itheima.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dsapa
 * @date 2023/4/10 21:01
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Info {

    private String text; //回答文本(期望的回复内容) \n可换行 空字符串则不回复

}
