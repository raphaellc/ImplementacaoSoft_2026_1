package com.gerenciadortarefas.auth.model;

import java.util.Optional;

public interface MfaPendingRepository {
    void criar(MfaPending pending);
    Optional<MfaPending> consumir(String tokenHash);
}
