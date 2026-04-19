package br.edu.projeto.model;

import br.edu.projeto.util.DatabaseConnectionH2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroRepositoryH2 implements LivroRepository {

    public LivroRepositoryH2() {
        criarTabelaSeNaoExistir();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS livros (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "titulo VARCHAR(255) NOT NULL, " +
                     "autor VARCHAR(255) NOT NULL, " +
                     "lido BOOLEAN DEFAULT FALSE)";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco de dados H2", e);
        }
    }

    @Override
    public Livro adicionarLivro(String titulo, String autor) {
        String sql = "INSERT INTO livros (titulo, autor, lido) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, titulo);
            pstmt.setString(2, autor);
            pstmt.setBoolean(3, false);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Livro(rs.getInt(1), titulo, autor, false);
                }
            }
            throw new RuntimeException("Erro ao obter ID gerado para o livro");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar livro", e);
        }
    }

    @Override
    public boolean atualizarLivro(Livro livroAtualizado) {
        String sql = "UPDATE livros SET titulo = ?, autor = ?, lido = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, livroAtualizado.titulo());
            pstmt.setString(2, livroAtualizado.autor());
            pstmt.setBoolean(3, livroAtualizado.lido());
            pstmt.setInt(4, livroAtualizado.id());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar livro", e);
        }
    }

    @Override
    public List<Livro> listarLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, lido FROM livros";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                livros.add(new Livro(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getBoolean("lido")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar livros", e);
        }
        return livros;
    }

    @Override
    public Optional<Livro> buscarPorId(int id) {
        String sql = "SELECT id, titulo, autor, lido FROM livros WHERE id = ?";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Livro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getBoolean("lido")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar livro", e);
        }
        return Optional.empty();
    }
}