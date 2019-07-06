package com.sky.framework.generator.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class MapperAnnotationPlugin extends PluginAdapter {

    private FullyQualifiedJavaType mapper;

    private FullyQualifiedJavaType repository;

    public MapperAnnotationPlugin() {
        mapper = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");
        repository = new FullyQualifiedJavaType("org.springframework.stereotype.Repository");
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(mapper);
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(repository);
        interfaze.addAnnotation("@Repository");
        return true;
    }

}
