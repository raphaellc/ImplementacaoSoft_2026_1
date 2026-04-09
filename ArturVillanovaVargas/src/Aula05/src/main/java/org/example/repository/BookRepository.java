package org.example.repository;

import org.example.database.DatabaseConnection;
import org.example.interfaces.IRepository;
import org.example.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository implements IRepository {

    public BookRepository() {
        createTable();
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS books (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "available BOOLEAN DEFAULT TRUE)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement())
        {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela H2: " + e.getMessage());
        }
    }

    @Override
    public void add(String title) {
        String sql = "INSERT INTO books (title, available) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setBoolean(2, true);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar livro no banco de dados: " + e.getMessage());
        }
    }

    @Override
    public boolean update(Book book) {
        String sql = "UPDATE books SET title = ?, available = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, book.title());
            pstmt.setBoolean(2, book.available());
            pstmt.setInt(3, book.id());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar livro no banco de dados: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean remove(Book book) {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, book.id());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar livro no banco de dados: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Book> getById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Book(rs.getInt("id"), rs.getString("title"), rs.getBoolean("available")));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar livro no banco de dados: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Book> getAll()
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getBoolean("available")));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas no banco de dados: " + e.getMessage());
        }
        return books;
    }
}