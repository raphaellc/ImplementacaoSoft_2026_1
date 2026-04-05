package br.edu.projeto.model;

import br.edu.projeto.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroRepository {

    /**
     * Adiciona um novo livro ao banco de dados.
     * Retorna o ID gerado, ou -1 em caso de falha.
     */
    public int addBook(String titulo, String autor, String isbn) {
        String sql = "INSERT INTO livros (titulo, autor, isbn, disponivel) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, titulo);
            pstmt.setString(2, autor);
            pstmt.setString(3, isbn);
            pstmt.setBoolean(4, true);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar livro no banco de dados: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Atualiza os dados de um livro existente.
     * Retorna true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean updateBook(Livro livroAtualizado) {
        if (livroAtualizado == null) return false;
        String sql = "UPDATE livros SET titulo = ?, autor = ?, isbn = ?, disponivel = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, livroAtualizado.titulo());
            pstmt.setString(2, livroAtualizado.autor());
            pstmt.setString(3, livroAtualizado.isbn());
            pstmt.setBoolean(4, livroAtualizado.disponivel());
            pstmt.setInt(5, livroAtualizado.id());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar livro no banco de dados: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um livro pelo ID.
     * Retorna true se a remoção foi bem-sucedida, false caso contrário.
     */
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM livros WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao remover livro do banco de dados: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna todos os livros cadastrados.
     */
    public List<Livro> listBooks() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, isbn, disponivel FROM livros";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                livros.add(mapRowToLivro(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros do banco de dados: " + e.getMessage());
        }
        return livros;
    }

    /**
     * Busca um livro pelo ID.
     * Retorna Optional.empty() se não encontrado.
     */
    public Optional<Livro> findById(int id) {
        String sql = "SELECT id, titulo, autor, isbn, disponivel FROM livros WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToLivro(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar livro por ID no banco de dados: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Método auxiliar: mapeia uma linha do ResultSet para um objeto Livro.
     */
    private Livro mapRowToLivro(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String titulo = rs.getString("titulo");
        String autor = rs.getString("autor");
        String isbn = rs.getString("isbn");
        boolean disponivel = rs.getBoolean("disponivel");
        return new Livro(id, titulo, autor, isbn, disponivel);
    }
}