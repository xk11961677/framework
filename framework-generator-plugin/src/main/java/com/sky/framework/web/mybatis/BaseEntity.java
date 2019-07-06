package com.sky.framework.web.mybatis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The class Base entity.
 *
 * @author
 */
@Data
@SuppressWarnings("unused")
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 2393269568666085258L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 状态 0 有效 1 无效
     */
    private Integer disabled;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;

    /**
     * 创建人
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedTime;

    /**
     * 更新人
     */
    @Column(name = "updated_by")
    private String updatedBy;




    @Transient
    private Integer pageNum;

    @Transient
    private Integer pageSize;

    @Transient
    private String orderBy;

    /**
     * Is new boolean.
     *
     * @return the boolean
     */
    @Transient
    @JsonIgnore
    public boolean isNew() {
        return this.id == null;
    }


}
