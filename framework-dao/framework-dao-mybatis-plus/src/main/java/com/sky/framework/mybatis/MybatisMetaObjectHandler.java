/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
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
package com.sky.framework.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.framework.model.util.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 通用的字段填充，如createBy createDate这些字段的自动填充
 *
 * @author
 */
@Component
@ConditionalOnProperty(value = MybatisPlusProperties.MYBATIS_PLUS_META_USER_ENABLE, matchIfMissing = true)
@Slf4j
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    private String DEFAULT_USER = "system";

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill ....");
        String userName = getUserName();
        this.setInsertFieldValByName("createBy", userName, metaObject);
        this.setInsertFieldValByName("updateBy", userName, metaObject);
        Date date = new Date();
        this.setInsertFieldValByName("createTime", date, metaObject);
        this.setInsertFieldValByName("updateTime", date, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        String userName = getUserName();
        this.setInsertFieldValByName("updateBy", userName, metaObject);
        Date date = new Date();
        this.setUpdateFieldValByName("updateTime", date, metaObject);
    }


    private String getUserName() {
        String username = UserContextHolder.getInstance().getUsername();
        username = Objects.isNull(username) || "".equals(username) ? DEFAULT_USER : username;
        return username;
    }
}