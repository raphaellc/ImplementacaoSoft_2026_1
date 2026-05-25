package com.gerenciadortarefas.auth.model;

import com.gerenciadortarefas.util.DatabaseConnectionH2;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class MfaPendingRepositoryH2 implements MfaPendingRepository {

    public MfaPendingRepositoryH2() {
        criarTabela();
    }

    private void criarTabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS mfa_pending (
                    token_hash CHAR(64) PRIMARY KEY,
                    usuario_id INT NOT NULL,
                    expira_em TIMESTAMP NOT NULL
                )""";
        try (Connection c = DatabaseConnectionH2.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar tabela mfa_pending", e);
        }
    }

    @Override
    public void criar(MfaPending p) {
        String sql = "INSERT INTO mfa_pending (token_hash, usuario_id, expira_em) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.tokenHash());
            ps.setInt(2, p.usuarioId());
            ps.setTimestamp(3, Timestamp.valueOf(p.expiraEm()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar mfa_pending", e);
        }
    }

    /** Consome o token: lê e remove de forma atômica. Retorna vazio se expirado/inexistente. */
    @Override
    public Optional<MfaPending> consumir(String tokenHash) {
        String sel = "SELECT usuario_id, expira_em FROM mfa_pending WHERE token_hash = ?";
        String del = "DELETE FROM mfa_pending WHERE token_hash = ?";
        try (Connection c = DatabaseConnectionH2.getConnection()) {
            c.setAutoCommit(false);
            MfaPending found = null;
            try (PreparedStatement ps = c.prepareStatement(sel)) {
                ps.setString(1, tokenHash);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        LocalDateTime exp = rs.getTimestamp("expira_em").toLocalDateTime();
                        if (exp.isAfter(LocalDateTime.now())) {
                            found = new MfaPending(tokenHash, rs.getInt("usuario_id"), exp);
                        }
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(del)) {
                ps.setString(1, tokenHash);
                ps.executeUpdate();
            }
            c.commit();
            return Optional.ofNullable(found);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consumir mfa_pending", e);
        }
    }
}
