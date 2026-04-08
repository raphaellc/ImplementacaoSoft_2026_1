package com.gerenciadortarefas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Endereço do banco: jdbc:mysql://[host]:[porta]/[nome_do_banco]
    private static final String URL = System.getenv().getOrDefault(
            "DB_URL", "jdbc:mysql://localhost:3306/gerenciador_tarefa");
    // Credenciais via variáveis de ambiente — nunca hardcode em código-fonte
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        if (PASSWORD == null) {
            throw new SQLException("Variável de ambiente DB_PASSWORD não definida");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
