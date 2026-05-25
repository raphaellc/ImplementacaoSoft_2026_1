package com.gerenciadortarefas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Endereço do banco: jdbc:mysql://[host]:[porta]/[nome_do_banco]
    private static final String URL = "jdbc:mysql://localhost:3306/gerenciador_tarefa";
    private static final String USER = "root";
    private static final String PASSWORD = "DevMySQ!";

    public static Connection getConnection() throws SQLException {
        try {
            // Garante que o driver está carregado
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado", e);
        }
    }
}
