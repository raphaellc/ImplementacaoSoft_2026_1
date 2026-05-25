package com.gerenciadortarefas.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Pool de conexões H2 baseado em HikariCP.
 *
 * <p>Por padrão usa modo file (./data/tarefadb.mv.db) para que usuários e
 * sessões sobrevivam a reinicializações. Pode ser sobrescrito via env var
 * {@code H2_URL} (ex.: {@code jdbc:h2:mem:tarefadb;DB_CLOSE_DELAY=-1}).
 */
public class DatabaseConnectionH2 {

    private static final String URL =
            System.getenv().getOrDefault("H2_URL", "jdbc:h2:file:./data/tarefadb;AUTO_SERVER=TRUE;MODE=LEGACY");
    private static final String USER     = System.getenv().getOrDefault("H2_USER", "sa");
    private static final String PASSWORD = System.getenv().getOrDefault("H2_PASSWORD", "");

    private static final HikariDataSource DATA_SOURCE = buildDataSource();

    private static HikariDataSource buildDataSource() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver H2 não encontrado no classpath", e);
        }
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(URL);
        cfg.setUsername(USER);
        cfg.setPassword(PASSWORD);
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setPoolName("h2-pool");
        cfg.setConnectionTimeout(5_000);
        return new HikariDataSource(cfg);
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
