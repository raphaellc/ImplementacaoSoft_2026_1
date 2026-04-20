package com.login.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.login.model.Usuario;

// Repository: Conexão direta com JDBC para entender o fluxo
public class UsuarioRepository {
    private static final String URL = "jdbc:h2:mem:logindb;DB_CLOSE_DELAY=-1";

    public UsuarioRepository() throws SQLException {
        try (Connection c = DriverManager.getConnection(URL, "sa", "")) {
            c.createStatement().execute("CREATE TABLE IF NOT EXISTS users(login VARCHAR(50), senha VARCHAR(50))");
            c.createStatement().execute("INSERT INTO users VALUES('admin', '1234')");
        }
    }

    public boolean validar(String login, String senha) throws SQLException {
        String sql = "SELECT * FROM users WHERE login = ? AND senha = ?";
        try (Connection c = DriverManager.getConnection(URL, "sa", "");
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            return ps.executeQuery().next();
        }
    }
}