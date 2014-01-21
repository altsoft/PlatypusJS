/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application;

import com.eas.client.ClientConstants;
import com.eas.client.SQLUtils;
import com.eas.client.sqldrivers.SqlDriver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class PlatypusUtils {

    public static final String COMMON_ENCODING_NAME = "utf-8";
    public static final String DATAFOLDER_REORDERABLE_PROP_NAME = "DataFolder.Index.reorderable";
    public static final String ELEMENTS_SOURCES_GROUP = "applicationElements";
    public static final String PLATYPUS_PROJECT_SOURCES_ROOT = "src";

    public static List<String> achieveSchemas(String aUrl, String aUser, String aPassword) throws Exception {
        String dialect = SQLUtils.dialectByUrl(aUrl);
        SqlDriver driver = SQLUtils.getSqlDriver(dialect);
        List<String> schemas = new ArrayList<>();
        try (java.sql.Connection conn = DriverManager.getConnection(aUrl, aUser, aPassword)) {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(driver.getSql4SchemasEnumeration())) {
                    while (rs.next()) {
                        String schema = rs.getString(ClientConstants.JDBCCOLS_TABLE_SCHEM);
                        if (schema != null) {
                            schemas.add(schema);
                        }
                    }
                    return schemas;
                }
            }
        }  
    }
    
    public static void createSchema(String aUrl, String aUser, String aPassword, String aSchema) throws Exception {
        String dialect = SQLUtils.dialectByUrl(aUrl);
        SqlDriver driver = SQLUtils.getSqlDriver(dialect);
        try (java.sql.Connection conn = DriverManager.getConnection(aUrl, aUser, aPassword)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(driver.getSql4CreateSchema(aSchema, null));
            }
        }
    }
    
}
