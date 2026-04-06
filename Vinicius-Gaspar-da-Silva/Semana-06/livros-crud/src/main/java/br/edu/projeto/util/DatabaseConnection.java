package br.edu.projeto.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/livros_crud";
    private static final String USER = "root"; 
    private static final String PASS = "Não vou botar minha senha aqui, bota a que tu criou"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}