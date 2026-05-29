package com.socialwork.model;

import com.socialwork.config.DatabaseInitializer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    public static Connection getConnection() throws SQLException {
        DatabaseInitializer.initialize();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found", e);
        }

        String mysqlUrl = System.getenv("MYSQL_URL");
        if (mysqlUrl != null && !mysqlUrl.isEmpty()) {
            if (mysqlUrl.startsWith("mysql://")) {
                mysqlUrl = "jdbc:" + mysqlUrl;
                if (!mysqlUrl.contains("?")) {
                    mysqlUrl += "?allowPublicKeyRetrieval=true&useSSL=true&requireSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true";
                }
            }
            return DriverManager.getConnection(mysqlUrl);
        }

        String host = getEnvOrDefault("MYSQLHOST", getEnvOrDefault("DB_HOST", "localhost"));
        String port = getEnvOrDefault("MYSQLPORT", getEnvOrDefault("DB_PORT", "3306"));
        String user = getEnvOrDefault("MYSQLUSER", getEnvOrDefault("DB_USER", "root"));
        String pass = getEnvOrDefault("MYSQLPASSWORD", getEnvOrDefault("DB_PASSWORD", "root"));

        boolean isRailway = System.getenv("MYSQLHOST") != null && !System.getenv("MYSQLHOST").isEmpty();
        String db = getEnvOrDefault("MYSQLDATABASE", getEnvOrDefault("DB_NAME", isRailway ? "railway" : "socialwork"));
        String sslParam = getEnvOrDefault("MYSQL_SSL", isRailway ? "true" : "false");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=" + sslParam + "&allowPublicKeyRetrieval=true&serverTimezone=UTC"
                + "&characterEncoding=UTF-8&useUnicode=true";

        return DriverManager.getConnection(url, user, pass);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String val = System.getenv(key);
        return (val != null && !val.isEmpty()) ? val : defaultValue;
    }
}
