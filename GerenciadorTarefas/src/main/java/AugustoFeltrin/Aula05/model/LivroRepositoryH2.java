package AugustoFeltrin.Aula05.model;

import AugustoFeltrin.Aula05.util.DatabaseConnectionH2;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroRepositoryH2 implements LivroRepository {
    public LivroRepositoryH2(){
        criarTabelaSeNaoExisitir();
    }

    private void criarTabelaSeNaoExisitir(){
        String sql = "CREATE TABLE IF NOT EXISTS livros (" + 
        "id INT AUTO_INCREMENT PRIMARY KEY, " +
        "titulo VARCHAR(255) NOT NULL, " +
        "concluida BOOLEAN DEFAULT FALSE)";
    try(Connection conn = DatabaseConnectionH2.getConnection();
        Statement smmt = conn.createStatement()) {
            smmt.execute(sql);
        } catch (SQLException e){
            throw new RuntimeException("Erro ao iniicalizar banco H2", e);
        }
    }

    @Override
    public void adicionar(String titulo){
        String sql = "INSERT INTO livros (titulo, concluida) VALUES (?, ?)";
        try(Connection conn = DatabaseConnectionH2.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, titulo);
                pstmt.setBoolean(2, false);
                pstmt.executeUpdate();
            } catch (SQLException e){
                throw new RuntimeException("Erro ao adicionar livro", e);
            }
    }

   @Override
    public List<Livro> listarLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT * FROM livros";
        try (Connection conn = DatabaseConnectionH2.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                livros.add(new Livro(rs.getInt("id"), rs.getString("titulo"), rs.getBoolean("concluida")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar livros", e);
        }
        return livros;
    }

    @Override
    public Optional<Livro> buscarPorID(int id) {
        String sql = "SELECT * FROM livros WHERE id = ?";
        try (Connection conn = DatabaseConnectionH2.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Livro(rs.getInt("id"), rs.getString("titulo"), rs.getBoolean("concluida")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar livro", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean atualizar(Livro livro){
        String sql = "UPDATE livros SET titulo = ?, concluida = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionH2.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, livro.titulo());
            pstmt.setBoolean(2, livro.concluida());
            pstmt.setInt(3, livro.id());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar livro", e);
        }
    }

    @Override
    public boolean remover(int id) {
    String sql = "DELETE FROM livros WHERE id = ?";

    try (Connection conn = DatabaseConnectionH2.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);

        return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover livro", e);
        }
    }
}
