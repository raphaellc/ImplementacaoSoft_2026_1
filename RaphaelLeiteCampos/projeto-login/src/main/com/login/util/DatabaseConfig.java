package com.aula.login.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * UTILITÁRIO DE BANCO DE DADOS
 * ============================
 * Configura o pool de conexões e cria as tabelas iniciais.
 *
 * Usamos H2 em modo "file" para persistência entre execuções.
 * Em produção seria trocado por PostgreSQL/MySQL mudando apenas
 * a URL e o driver — o resto da aplicação não muda. Isso é
 * o princípio da inversão de dependências na prática!
 *
 * Singleton Pattern: garante uma única instância do pool.
 */
public class DatabaseConfig {

    private static HikariDataSource dataSource;

    // Construtor privado — impede instanciação direta
    private DatabaseConfig() {}

    public static DataSource getDataSource() {
        if (dataSource == null) {
            inicializar();
        }
        return dataSource;
    }

    private static synchronized void inicializar() {
        if (dataSource != null) return;

        HikariConfig config = new HikariConfig();

        // H2 em arquivo — persiste os dados mesmo após reiniciar a JVM
        config.setJdbcUrl("jdbc:h2:./dados/logindb;AUTO_SERVER=TRUE");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(3000);

        dataSource = new HikariDataSource(config);

        criarTabelas();
        System.out.println("✓ Banco de dados inicializado em ./dados/logindb.mv.db");
    }

    private static void criarTabelas() {
        String sql = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                    nome       VARCHAR(100)  NOT NULL,
                    email      VARCHAR(150)  NOT NULL UNIQUE,
                    senha_hash VARCHAR(255)  NOT NULL,
                    ativo      BOOLEAN       NOT NULL DEFAULT TRUE,
                    criado_em  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
                );
                """;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Tabela 'usuarios' verificada/criada.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas: " + e.getMessage(), e);
        }
    }

    public static void fechar() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
