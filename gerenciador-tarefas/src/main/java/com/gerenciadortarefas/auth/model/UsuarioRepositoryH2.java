package com.gerenciadortarefas.auth.model;

import com.gerenciadortarefas.util.DatabaseConnectionH2;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class UsuarioRepositoryH2 implements UsuarioRepository {

    public UsuarioRepositoryH2() {
        criarTabela();
    }

    private void criarTabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    username VARCHAR(60) NOT NULL UNIQUE,
                    senha_hash VARCHAR(255) NOT NULL,
                    mfa_habilitado BOOLEAN DEFAULT FALSE,
                    otp_secret_cifrado VARBINARY(512),
                    ultimo_login TIMESTAMP NULL,
                    ultimo_logout TIMESTAMP NULL
                )""";
        try (Connection c = DatabaseConnectionH2.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar tabela usuarios", e);
        }
    }

    @Override
    public Usuario criar(String email, String username, String senhaHash) {
        String sql = "INSERT INTO usuarios (email, username, senha_hash) VALUES (?, ?, ?)";
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, email);
            p.setString(2, username);
            p.setString(3, senhaHash);
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt(1), email, username, senhaHash, false, null, null, null);
                }
            }
            throw new RuntimeException("Falha ao obter id gerado");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return buscar("SELECT * FROM usuarios WHERE id = ?", String.valueOf(id), true);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        String sql = "SELECT * FROM usuarios WHERE email = ? OR username = ?";
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, login);
            p.setString(2, login);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
        return Optional.empty();
    }

    private Optional<Usuario> buscar(String sql, String valor, boolean isInt) {
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            if (isInt) p.setInt(1, Integer.parseInt(valor)); else p.setString(1, valor);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
        return Optional.empty();
    }

    @Override
    public void atualizarUltimoLogin(int usuarioId, LocalDateTime quando) {
        atualizarTimestamp("UPDATE usuarios SET ultimo_login = ? WHERE id = ?", quando, usuarioId);
    }

    @Override
    public void atualizarUltimoLogout(int usuarioId, LocalDateTime quando) {
        atualizarTimestamp("UPDATE usuarios SET ultimo_logout = ? WHERE id = ?", quando, usuarioId);
    }

    private void atualizarTimestamp(String sql, LocalDateTime quando, int id) {
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setTimestamp(1, Timestamp.valueOf(quando));
            p.setInt(2, id);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar timestamp", e);
        }
    }

    @Override
    public void definirSegredoMfa(int usuarioId, byte[] otpSecretCifrado, boolean habilitado) {
        String sql = "UPDATE usuarios SET otp_secret_cifrado = ?, mfa_habilitado = ? WHERE id = ?";
        try (Connection c = DatabaseConnectionH2.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setBytes(1, otpSecretCifrado);
            p.setBoolean(2, habilitado);
            p.setInt(3, usuarioId);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao definir segredo MFA", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Timestamp ulIn  = rs.getTimestamp("ultimo_login");
        Timestamp ulOut = rs.getTimestamp("ultimo_logout");
        return new Usuario(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("senha_hash"),
                rs.getBoolean("mfa_habilitado"),
                rs.getBytes("otp_secret_cifrado"),
                ulIn  == null ? null : ulIn.toLocalDateTime(),
                ulOut == null ? null : ulOut.toLocalDateTime()
        );
    }
}
