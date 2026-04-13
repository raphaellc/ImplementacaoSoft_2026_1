package br.edu.projeto.model;
import br.edu.projeto.model.Livro;
import br.edu.projeto.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação MySQL do LivroRepository.
 */
public class LivroRepositoryMySQL implements LivroRepository {

    @Override
    public int adicionarLivro(String titulo, String autor, String isbn) {
        String sql = "INSERT INTO gerenciador_livro.livros (titulo, autor, isbn, disponivel) VALUES (?, ?, ?, ?)";
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

    @Override
    public boolean atualizarLivro(Livro livroAtualizado) {
        if (livroAtualizado == null) return false;
        String sql = "UPDATE gerenciador_livro.livros SET titulo = ?, autor = ?, isbn = ?, disponivel = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, livroAtualizado.titulo());
            pstmt.setString(2, livroAtualizado.autor());
            pstmt.setString(3, livroAtualizado.isbn());
            pstmt.setBoolean(4, livroAtualizado.disponivel());
            pstmt.setInt(5, livroAtualizado.id());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar livro no banco de dados: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removerLivro(int id) {
        String sql = "DELETE FROM gerenciador_livro.livros WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao remover livro do banco de dados: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Livro> listarLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, isbn, disponivel FROM gerenciador_livro.livros";
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

    @Override
    public Optional<Livro> buscarPorId(int id) {
        String sql = "SELECT id, titulo, autor, isbn, disponivel FROM gerenciador_livro.livros WHERE id = ?";
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

    private Livro mapRowToLivro(ResultSet rs) throws SQLException {
        return new Livro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getBoolean("disponivel")
        );
    }
}