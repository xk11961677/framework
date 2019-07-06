package com.sky.framework.model.dto;

import lombok.Data;

/**
 * 描述:
 *
 * @author
 * @version V1.0
 */
@SuppressWarnings("serial")
@Data
public class BaseQueryPageResponseDto extends BaseResponseDto {

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 分页使用的参数，分页大小
     */
    private int pageSize;

    /**
     * 分页使用的参数，当前分页号
     */
    private int pageNum;

    /**
     * 分页使用的参数，总数据条数
     */
    private int total;

    /**
     * 分页使用的参数，总页数
     */
    private int pages;



}
