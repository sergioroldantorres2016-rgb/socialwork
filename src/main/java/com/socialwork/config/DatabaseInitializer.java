package com.socialwork.config;

import com.socialwork.model.ConexionBD;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try (InputStream is = getClass().getResourceAsStream("/init.sql")) {
            if (is == null) {
                event.getServletContext().log("DatabaseInitializer: init.sql not found, skipping");
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
                event.getServletContext().log("DatabaseInitializer: schema initialized successfully");
            }
        } catch (Exception e) {
            event.getServletContext().log("DatabaseInitializer error: " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}