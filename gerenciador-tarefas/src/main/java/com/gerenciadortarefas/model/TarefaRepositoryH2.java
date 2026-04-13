package com.gerenciadortarefas.model;

import com.gerenciadortarefas.util.DatabaseConnectionH2;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaRepositoryH2 implements TarefaRepository {

    public TarefaRepositoryH2() {
        criarTabelaSeNaoExistir();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS tarefas (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "descricao VARCHAR(255) NOT NULL, " +
                     "concluida BOOLEAN DEFAULT FALSE)";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            // Não expõe detalhes da exceção SQL ao chamador
            throw new RuntimeException("Erro ao inicializar banco de dados H2", e);
        }
    }

    @Override
    public void adicionarTarefa(String descricao) {
        String sql = "INSERT INTO tarefas (descricao, concluida) VALUES (?, ?)";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, descricao);
            pstmt.setBoolean(2, false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar tarefa", e);
        }
    }

    @Override
    public boolean atualizarTarefa(Tarefa tarefaAtualizada) {
        String sql = "UPDATE tarefas SET descricao = ?, concluida = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tarefaAtualizada.descricao());
            pstmt.setBoolean(2, tarefaAtualizada.concluida());
            pstmt.setInt(3, tarefaAtualizada.id());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tarefa", e);
        }
    }

    @Override
    public List<Tarefa> listarTarefas() {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas";
        try (Connection conn = DatabaseConnectionH2.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tarefas.add(new Tarefa(rs.getInt("id"), rs.getString("descricao"), rs.getBoolean("concluida")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas", e);
        }
        return tarefas;
    }

    @Override
    public Optional<Tarefa> buscarPorId(int id) {
        String sql = "SELECT * FROM tarefas WHERE id = ?";
        // Falha 4 corrigida: ResultSet incluído no try-with-resources para garantir fechamento explícito
        try (Connection conn = DatabaseConnectionH2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Tarefa(rs.getInt("id"), rs.getString("descricao"), rs.getBoolean("concluida")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefa", e);
        }
        return Optional.empty();
    }
}
