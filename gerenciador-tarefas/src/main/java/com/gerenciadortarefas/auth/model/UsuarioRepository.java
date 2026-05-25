package com.gerenciadortarefas.auth.model;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario criar(String email, String username, String senhaHash);
    Optional<Usuario> buscarPorId(int id);
    Optional<Usuario> buscarPorLogin(String login);
    void atualizarUltimoLogin(int usuarioId, LocalDateTime quando);
    void atualizarUltimoLogout(int usuarioId, LocalDateTime quando);
    void definirSegredoMfa(int usuarioId, byte[] otpSecretCifrado, boolean habilitado);
}
