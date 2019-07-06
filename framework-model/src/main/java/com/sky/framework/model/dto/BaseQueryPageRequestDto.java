package com.sky.framework.model.dto;

import lombok.Data;

/**
 * 分页查询
 *
 * @author
 */
@SuppressWarnings("serial")
@Data
public class BaseQueryPageRequestDto extends BaseRequestDto {
    /**
     * 分页使用的参数，分页大小
     */
    private Integer pageSize = 10;

    /**
     * 分页使用的参数，当前分页号
     */
    private Integer pageNum = 1;

}
