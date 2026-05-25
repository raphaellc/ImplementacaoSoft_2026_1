package com.gerenciadortarefas.auth.model;

import com.gerenciadortarefas.util.DatabaseConnectionH2;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class SessaoRepositoryH2 implements SessaoRepository {

    public SessaoRepositoryH2() {
        criarTabela();
    }

    private void criarTabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS sessoes (
                    token_hash CHAR(64) PRIMARY KEY,
                    usuario_id INT NOT NULL,
                    criado_em TIMESTAMP NOT NULL,
                    last_activity TIMESTAMP NOT NULL,
                    ip_address VARCHAR(45),
                    user_agent VARCHAR(255),
                    csrf_token VARCHAR(64) NOT NULL,
                    revogada_em TIMESTAMP NULL,
                    motivo_logout VARCHAR(100) NULL,
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                )""";
        try (Connection c = DatabaseConnectionH2.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar tabela sessoes", e);
        }
    }

    @Override
    public void criar(Sessao s) {
        String sql = """
                INSERT INTO sessoes (token_hash, usuario_id, criado_em, last_activity,
                                     ip_address, user_agent, csrf_token)
                VALUES (?, ?, ?, ?, ?, ?, ?)""";
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, s.tokenHash());
            p.setInt(2, s.usuarioId());
            p.setTimestamp(3, Timestamp.valueOf(s.criadoEm()));
            p.setTimestamp(4, Timestamp.valueOf(s.lastActivity()));
            p.setString(5, s.ipAddress());
            p.setString(6, s.userAgent());
            p.setString(7, s.csrfToken());
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar sessão", e);
        }
    }

    @Override
    public Optional<Sessao> buscarPorTokenHash(String tokenHash) {
        String sql = "SELECT * FROM sessoes WHERE token_hash = ?";
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, tokenHash);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Timestamp rev = rs.getTimestamp("revogada_em");
                    return Optional.of(new Sessao(
                            rs.getString("token_hash"),
                            rs.getInt("usuario_id"),
                            rs.getTimestamp("criado_em").toLocalDateTime(),
                            rs.getTimestamp("last_activity").toLocalDateTime(),
                            rs.getString("ip_address"),
                            rs.getString("user_agent"),
                            rs.getString("csrf_token"),
                            rev == null ? null : rev.toLocalDateTime(),
                            rs.getString("motivo_logout")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessão", e);
        }
        return Optional.empty();
    }

    @Override
    public void atualizarAtividade(String tokenHash, LocalDateTime quando) {
        String sql = "UPDATE sessoes SET last_activity = ? WHERE token_hash = ? AND revogada_em IS NULL";
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setTimestamp(1, Timestamp.valueOf(quando));
            p.setString(2, tokenHash);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar atividade", e);
        }
    }

    @Override
    public void encerrar(String tokenHash, String motivo, LocalDateTime quando) {
        String sql = "UPDATE sessoes SET revogada_em = ?, motivo_logout = ? WHERE token_hash = ? AND revogada_em IS NULL";
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setTimestamp(1, Timestamp.valueOf(quando));
            p.setString(2, motivo);
            p.setString(3, tokenHash);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao encerrar sessão", e);
        }
    }
}
