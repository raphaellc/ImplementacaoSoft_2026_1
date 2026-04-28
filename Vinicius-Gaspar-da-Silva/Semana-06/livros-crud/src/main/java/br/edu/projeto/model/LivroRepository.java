package br.edu.projeto.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.projeto.util.DatabaseConnection;

public class LivroRepository {
    public void adicionarLivro(String titulo, String autor) {
        String sql = "INSERT INTO livros (titulo, autor) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, titulo);
            stmt.setString(2, autor);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar no banco: " + e.getMessage());
        }
    }

    public List<Livro> listarLivros() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM livros";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(new Livro(
                    rs.getInt("id"), 
                    rs.getString("titulo"), 
                    rs.getString("autor")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar dados: " + e.getMessage());
        }
        return lista;
    }
}