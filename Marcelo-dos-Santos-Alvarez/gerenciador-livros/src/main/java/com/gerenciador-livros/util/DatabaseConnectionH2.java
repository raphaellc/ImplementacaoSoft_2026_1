package com.gerenciadorlivros.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionH2 {
	// DB_CLOSE_DELAY=-1 mantém o banco na memória enquanto a JVM estiver rodando
	private static final String URL = "jdbc:h2:mem:livrodb;DB_CLOSE_DELAY=-1";
	private static final String USER = System.getenv().getOrDefault("H2_USER", "sa");
	private static final String PASSWORD = System.getenv().getOrDefault("H2_PASSWORD", "");

	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Driver H2 não encontrado no classpath", e);
		}

		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
