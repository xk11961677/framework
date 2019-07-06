package com.sky.framework.generator.plugin;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.internal.JDBCConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class CommentPlugin extends PluginAdapter {

    private static final String AUTHOR = "modelClassAuthor";

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType) {
//        String remark = introspectedColumn.getRemarks();
//        field.addJavaDocLine("/** " + remark + " */");
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        addModelClassComment(topLevelClass, introspectedTable);
        return true;
    }

    private void addModelClassComment(TopLevelClass topLevelClass,
                                      IntrospectedTable introspectedTable) {
        String remarks = "";
        String author = getProperties().getProperty(AUTHOR);
        if (null == author || "".equals(author)) {
            author = System.getProperty("user.name");
        }

        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        try {
            JDBCConnectionConfiguration jdbcConnectionConfiguration = context.getJdbcConnectionConfiguration();
            jdbcConnectionConfiguration.addProperty("remarks", "true");
            jdbcConnectionConfiguration.addProperty("useInformationSchema", "true");
            Connection connection = new JDBCConnectionFactory(jdbcConnectionConfiguration).getConnection();
            ResultSet rs = connection.getMetaData().getTables(table.getIntrospectedCatalog(),table.getIntrospectedSchema(), table.getIntrospectedTableName(), null);

            if (null != rs && rs.next()) {
                remarks = rs.getString("REMARKS");
            }
            closeConnection(connection, rs);
        } catch (SQLException e) {}

        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + remarks);
//        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * @author " + author);
        topLevelClass.addJavaDocLine(" * @date " + format.format(new Date()));
//        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
                                                      IntrospectedTable introspectedTable) {
        addModelClassComment(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private void closeConnection(Connection connection, ResultSet rs) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {}
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {}
        }

    }
}
