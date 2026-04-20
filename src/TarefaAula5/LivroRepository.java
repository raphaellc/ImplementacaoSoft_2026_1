package TarefaAula5;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SqlNoDataSourceInspection")
public class LivroRepository {
    private static final String URL = "jdbc:h2:./livrosdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public LivroRepository() {
        configurarBanco();
        limparTudo(); // Limpa a base de dados a cada nova execução
    }

    private void configurarBanco() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS LIVROS (ID INT AUTO_INCREMENT PRIMARY KEY, TITULO VARCHAR(255), ALUGADO BOOLEAN)");
        } catch (SQLException e) {
            System.err.println("Erro ao configurar banco: " + e.getMessage());
        }
    }

    public void limparTudo() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE LIVROS");
        } catch (SQLException e) {
            System.err.println("Erro ao limpar dados: " + e.getMessage());
        }
    }

    public void AdicionarLivro(String titulo) {
        String sql = "INSERT INTO LIVROS (TITULO, ALUGADO) VALUES (?, FALSE)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, titulo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar livro: " + e.getMessage());
        }
    }

    public boolean marcarComoAlugado(int id) {
        return executarUpdate("UPDATE LIVROS SET ALUGADO = TRUE WHERE ID = ? AND ALUGADO = FALSE", id);
    }

    public boolean marcarComoDisponivel(int id) {
        return executarUpdate("UPDATE LIVROS SET ALUGADO = FALSE WHERE ID = ? AND ALUGADO = TRUE", id);
    }

    private boolean executarUpdate(String sql, int id) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro na operação de base de dados: " + e.getMessage());
            return false;
        }
    }

    public List<Livro> listarLivros() {
        List<Livro> lista = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM LIVROS")) {
            while (rs.next()) {
                lista.add(new Livro(rs.getInt("ID"), rs.getString("TITULO"), rs.getBoolean("ALUGADO")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros: " + e.getMessage());
        }
        return lista;
    }
}