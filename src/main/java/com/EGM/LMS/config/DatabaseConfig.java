package com.EGM.LMS.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;

@Slf4j
@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${spring.datasource.url:}") String configUrl,
            @Value("${spring.datasource.username:}") String configUsername,
            @Value("${spring.datasource.password:}") String configPassword,
            @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}") String driverClassName,
            Environment env) {

        String url = configUrl;
        String username = configUsername;
        String password = configPassword;

        // 1. Check if Render's DATABASE_URL or JDBC_DATABASE_URL environment variable is set
        String envJdbcUrl = env.getProperty("JDBC_DATABASE_URL");
        String envDatabaseUrl = env.getProperty("DATABASE_URL");

        if (StringUtils.hasText(envJdbcUrl)) {
            url = envJdbcUrl.trim();
        } else if (StringUtils.hasText(envDatabaseUrl) && (!StringUtils.hasText(url) || url.contains("localhost"))) {
            url = envDatabaseUrl.trim();
        }

        // 2. Normalize PostgreSQL URL schemes to guarantee 'jdbc:postgresql://'
        if (StringUtils.hasText(url)) {
            url = url.trim();
            if (url.startsWith("postgres://")) {
                url = "jdbc:postgresql://" + url.substring("postgres://".length());
            } else if (url.startsWith("postgresql://")) {
                url = "jdbc:postgresql://" + url.substring("postgresql://".length());
            } else if (!url.startsWith("jdbc:")) {
                url = "jdbc:" + url;
            }
        }

        // 3. If credentials are embedded in URL (e.g. jdbc:postgresql://user:pass@host:port/db),
        // extract them so connection pool and driver handle them reliably
        if (StringUtils.hasText(url) && url.contains("@")) {
            try {
                String uriPart = url.substring("jdbc:".length());
                URI uri = URI.create(uriPart);
                if (uri.getUserInfo() != null) {
                    String[] parts = uri.getUserInfo().split(":", 2);
                    if (!StringUtils.hasText(username)) {
                        username = parts[0];
                    }
                    if (parts.length > 1 && !StringUtils.hasText(password)) {
                        password = parts[1];
                    }
                    String portPart = uri.getPort() > 0 ? ":" + uri.getPort() : "";
                    String queryPart = uri.getQuery() != null ? "?" + uri.getQuery() : "";
                    url = "jdbc:postgresql://" + uri.getHost() + portPart + uri.getPath() + queryPart;
                }
            } catch (Exception e) {
                log.warn("[DatabaseConfig] Could not parse embedded credentials from URL, proceeding with URL as-is: {}", e.getMessage());
            }
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(StringUtils.hasText(driverClassName)
                ? driverClassName
                : "org.postgresql.Driver");

        if (StringUtils.hasText(username)) {
            hikariConfig.setUsername(username);
        }
        if (StringUtils.hasText(password)) {
            hikariConfig.setPassword(password);
        }

        // Resilient settings for containerized deployment
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(300000);
        hikariConfig.setConnectionTimeout(20000);

        String maskedUrl = url != null ? url.replaceAll(":[^:@/]+@", ":***@") : "null";
        log.info("[DatabaseConfig] Initializing PostgreSQL DataSource with URL: {}, user: {}", maskedUrl, username);

        return new HikariDataSource(hikariConfig);
    }
}
