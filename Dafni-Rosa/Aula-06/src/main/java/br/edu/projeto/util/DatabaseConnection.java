package br.edu.projeto.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Incompleto ainda 


    // Endereço do banco: jdbc:mysql://[host]:[porta]/[nome_do_banco]
    private static final String URL = "jdbc:mysql://localhost:3306/gerenciador_livros";
    private static final String USER = "root";
    private static final String PASSWORD = "SuaSenhaAqui";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}