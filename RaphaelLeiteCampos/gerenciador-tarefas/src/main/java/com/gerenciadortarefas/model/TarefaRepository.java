package com.gerenciadortarefas.model;

import com.gerenciadortarefas.model.Tarefa;
import com.gerenciadortarefas.util.DatabaseConnection;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class TarefaRepository {
    private List<Tarefa> tarefas;
    private int proximoId;

    public TarefaRepository(){
        this.tarefas = new ArrayList<>();
        this.proximoId = 1;
    }
    public void adicionarTarefa(String descricao){
        Tarefa tarefa = new Tarefa(proximoId++, descricao, false);
        tarefas.add(tarefa);
        String sql = "INSERT INTO tarefas (descricao, concluida) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, descricao);
            pstmt.setBoolean(2, false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar tarefa no banco de dados: " + e.getMessage());
        }
    }

    public boolean atualizarTarefa(Tarefa tarefaAtualizada){
        if (tarefaAtualizada == null) return false;

        for (int i = 0; i < tarefas.size(); i++) {
            // Comparamos apenas o ID, que é o que identifica a tarefa
            if (tarefas.get(i).id() == tarefaAtualizada.id()) {
                tarefas.set(i, tarefaAtualizada);
                return true;
            }
        String sql = "UPDATE tarefas SET descricao = ?, concluida = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tarefaAtualizada.descricao());
            pstmt.setBoolean(2, tarefaAtualizada.concluida());
            pstmt.setInt(3, tarefaAtualizada.id());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar tarefa no banco de dados: " + e.getMessage());
            return false;
        }
        return false;
    }

    public List<Tarefa> listarTarefas(){
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT id, descricao, concluida FROM tarefas";
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
            System.out.println("Erro ao listar tarefas do banco de dados: " + e.getMessage());
        }
        return tarefas;
    }

    public Optional<Tarefa> buscarPorId(int id) {
        String sql = "SELECT id, descricao, concluida FROM tarefas WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String descricao = rs.getString("descricao");
                boolean concluida = rs.getBoolean("concluida");
                return Optional.of(new Tarefa(id, descricao, concluida));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar tarefa por ID no banco de dados: " + e.getMessage());
        }
        return Optional.empty();
    }
}
