package com.gerenciadortarefas.model;

import com.gerenciadortarefas.util.DatabaseConnection;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class TarefaRepositoryMySQL implements TarefaRepository{

    public void adicionarTarefa(String descricao){
        String sql = "INSERT INTO gerenciador_tarefa.tarefas (descricao, concluida) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, descricao);
            pstmt.setBoolean(2, false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Corrigido: relança como RuntimeException em vez de engolir silenciosamente com println
            throw new RuntimeException("Erro ao adicionar tarefa", e);
        }
    }

    public boolean atualizarTarefa(Tarefa tarefaAtualizada){
        if (tarefaAtualizada == null) return false;
        String sql = "UPDATE gerenciador_tarefa.tarefas SET descricao = ?, concluida = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tarefaAtualizada.descricao());
            pstmt.setBoolean(2, tarefaAtualizada.concluida());
            pstmt.setInt(3, tarefaAtualizada.id());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tarefa", e);
        }
    }

    public List<Tarefa> listarTarefas(){
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT id, descricao, concluida FROM gerenciador_tarefa.tarefas";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String descricao = rs.getString("descricao");
                boolean concluida = rs.getBoolean("concluida");
                tarefas.add(new Tarefa(id, descricao, concluida));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas", e);
        }
        return tarefas;
    }

    public Optional<Tarefa> buscarPorId(int id) {
        String sql = "SELECT id, descricao, concluida FROM gerenciador_tarefa.tarefas WHERE id = ?";
        // Corrigido: ResultSet no try-with-resources para garantir fechamento explícito
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String descricao = rs.getString("descricao");
                    boolean concluida = rs.getBoolean("concluida");
                    return Optional.of(new Tarefa(id, descricao, concluida));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefa", e);
        }
        return Optional.empty();
    }
}
