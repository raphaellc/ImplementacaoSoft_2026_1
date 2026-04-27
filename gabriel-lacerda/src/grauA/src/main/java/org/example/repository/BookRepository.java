package org.example.repository;

import org.example.database.DatabaseConnection;
import org.example.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {

    public BookRepository() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS books (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "available BOOLEAN DEFAULT TRUE)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    public void addBook(String title) {
        String sql = "INSERT INTO books (title, available) VALUES (?, true)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar livro: " + e.getMessage());
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros: " + e.getMessage());
        }
        return books;
    }

    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar livro: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void update(Book book) {
        String sql = "UPDATE books SET available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, book.available());
            pstmt.setInt(2, book.id());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar livro: " + e.getMessage());
        }
    }
}