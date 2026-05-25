package com.gerenciadortarefas.model;

import com.gerenciadortarefas.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaRepositoryMySQL implements TarefaRepository {

    @Override
    public Tarefa adicionarTarefa(int usuarioId, String descricao){
        String sql = "INSERT INTO gerenciador_tarefa.tarefas (usuario_id, descricao, concluida) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setString(2, descricao);
            pstmt.setBoolean(3, false);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return new Tarefa(rs.getInt(1), usuarioId, descricao, false);
            }
            throw new RuntimeException("Erro ao obter ID gerado para a tarefa");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar tarefa", e);
        }
    }

    @Override
    public boolean atualizarTarefa(Tarefa t){
        if (t == null) return false;
        String sql = "UPDATE gerenciador_tarefa.tarefas SET descricao = ?, concluida = ? WHERE id = ? AND usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.descricao());
            pstmt.setBoolean(2, t.concluida());
            pstmt.setInt(3, t.id());
            pstmt.setInt(4, t.usuarioId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tarefa", e);
        }
    }

    @Override
    public boolean deletarTarefa(int usuarioId, int id) {
        String sql = "DELETE FROM gerenciador_tarefa.tarefas WHERE id = ? AND usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, usuarioId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar tarefa", e);
        }
    }

    @Override
    public List<Tarefa> listarTarefas(int usuarioId){
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT id, usuario_id, descricao, concluida FROM gerenciador_tarefa.tarefas WHERE usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tarefas.add(new Tarefa(rs.getInt("id"), rs.getInt("usuario_id"),
                            rs.getString("descricao"), rs.getBoolean("concluida")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas", e);
        }
        return tarefas;
    }

    @Override
    public Optional<Tarefa> buscarPorId(int usuarioId, int id) {
        String sql = "SELECT id, usuario_id, descricao, concluida FROM gerenciador_tarefa.tarefas WHERE id = ? AND usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Tarefa(rs.getInt("id"), rs.getInt("usuario_id"),
                            rs.getString("descricao"), rs.getBoolean("concluida")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefa", e);
        }
        return Optional.empty();
    }
}
