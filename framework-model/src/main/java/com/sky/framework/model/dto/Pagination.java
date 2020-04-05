/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 描述: 分页
 *
 * @author
 * @version V1.0
 */
public class Pagination<P> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -681184866199665872L;

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

    /**
     * 查询结果数据
     */
    private List<P> list = null;

    public Pagination() {
    }

    public Pagination(int pageSize, int pageNum, int totalCount, List<P> datas) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.total = totalCount;
        this.list = datas;
        if (this.pageSize == 0) {
            pages = 0;
        } else if (this.total % this.pageSize == 0) {
            pages = this.total / this.pageSize;
        } else {
            pages = totalCount / this.pageSize + 1;
        }
    }

    public Pagination(int pageSize, int pageNum, List<P> datas) {
        this(pageSize, pageNum, 0, datas);
    }

    public Pagination(int pageSize, int pageNum, int totalCount) {
        this(pageSize, pageNum, totalCount, null);
    }

    public Pagination(int pageSize, int pageNum) {
        this(pageSize, pageNum, 0, null);
    }

    /**
     * 内存分页 edit by YacoXiao
     */
    public void ramPage() {
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageNum > pages) {
            pageNum = pages;
        }
        int from = (pageNum - 1) * pageSize;
        int to = pageNum * pageSize ;
        if (pageNum == pages && pages * pageSize > total) {
            to = total;
        }
        this.list = total == 0 ? null : list.subList(from, to);
    }


    public int getPageSize() {
        return pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getTotal() {
        return total;
    }

    public int getPages() {
        return this.pages;
    }

    public List<P> getList() {
        return list;
    }

}
