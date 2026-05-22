package com.socialwork.config;

import com.socialwork.model.ConexionBD;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    private static volatile boolean initialized = false;

    public static synchronized void initialize() {
        if (initialized) return;
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/init.sql")) {
            if (is == null) {
                System.out.println("DatabaseInitializer: init.sql not found, skipping");
                return;
            }
            String sql = new String(is.readAllBytes(), "UTF-8");
            try (Connection c = ConexionBD.getConnection(); Statement st = c.createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        st.execute(trimmed);
                    }
                }
                initialized = true;
                System.out.println("DatabaseInitializer: schema initialized successfully");
            }
        } catch (Exception e) {
            System.out.println("DatabaseInitializer error: " + e.getMessage());
        }
    }
}