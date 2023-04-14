package com.itheima.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dsapa
 * @date 2023/4/10 20:47
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataInfo {

    private Integer type; //5000 回答类型为文本

    private Info info; //回答结果集合


}
