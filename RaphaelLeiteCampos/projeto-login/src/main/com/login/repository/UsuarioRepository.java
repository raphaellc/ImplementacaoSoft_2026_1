package com.login.repository;

import com.login.model.Usuario;
import com.login.util.DatabaseConfig;

import java.sql.*;
import java.util.Optional;

/**
 * CAMADA REPOSITORY (Persistência)
 * =================================
 * Responsabilidade ÚNICA: falar com o banco de dados.
 * Nenhuma regra de negócio aqui — apenas operações CRUD.
 *
 * Optional<T>: retorno elegante que evita NullPointerException.
 * Se o usuário não existe, retorna Optional.empty().
 * Se existe, retorna Optional.of(usuario).
 *
 * Boas práticas:
 *  - PreparedStatement previne SQL Injection
 *  - try-with-resources fecha conexões automaticamente
 *  - Separação total da lógica de negócio
 */
public class UsuarioRepository {

    /**
     * Busca um usuário pelo email.
     * Retorna Optional para forçar o chamador a tratar "não encontrado".
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT id, nome, email, senha_hash, ativo FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.toLowerCase().trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha_hash"),
                        rs.getBoolean("ativo")
                );
                return Optional.of(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Verifica se um email já está cadastrado.
     */
    public boolean emailJaCadastrado(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.toLowerCase().trim());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Salva um novo usuário no banco.
     * Retorna o usuário com o ID gerado pelo banco.
     */
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha_hash, ativo) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail().toLowerCase().trim());
            ps.setString(3, usuario.getSenhaHash());
            ps.setBoolean(4, usuario.isAtivo());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                usuario.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }

        return usuario;
    }
}
