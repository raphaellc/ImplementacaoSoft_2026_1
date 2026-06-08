package com.gerenciadortarefas.service;

import com.gerenciadortarefas.model.Tarefa;
import com.gerenciadortarefas.model.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

class TarefaServiceImplTest {

    private TarefaRepository repositoryMock;
    private TarefaServiceImpl tarefaService;

    @BeforeEach
    void setUp() {
        // Inicializa o mock do repositório antes de cada teste
        repositoryMock = Mockito.mock(TarefaRepository.class);
        tarefaService = new TarefaServiceImpl(repositoryMock);
    }

    @Test
    @DisplayName("TU-01: Deve adicionar tarefa com sucesso quando descrição for válida")
    void deveAdicionarTarefaComSucesso() {
        // Arrange (Preparação)
        int usuarioId = 1;
        String descricao = "Estudar JUnit 5";
        Tarefa tarefaSimulada = new Tarefa(100, usuarioId, descricao, false);
        
        // Define que quando o repositório for chamado, ele retorna a tarefa simulada
        Mockito.when(repositoryMock.adicionarTarefa(usuarioId, descricao))
               .thenReturn(tarefaSimulada);

        // Act (Ação)
        Tarefa resultado = tarefaService.adicionarTarefa(usuarioId, descricao);

        // Assert (Verificação)
        assertNotNull(resultado, "A tarefa retornada não deve ser nula");
        assertEquals(100, resultado.id());
        assertEquals(descricao, resultado.descricao());
        assertFalse(resultado.concluida());
        
        // Verifica se o método do repositório foi realmente chamado
        Mockito.verify(repositoryMock, Mockito.times(1)).adicionarTarefa(usuarioId, descricao);
    }

    @Test
    @DisplayName("TU-02: Deve lançar exceção quando a descrição for vazia ou nula")
    void deveLancarExcecaoQuandoDescricaoVazia() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tarefaService.adicionarTarefa(1, "   ")
        );

        assertEquals("A descrição não pode ser vazia", exception.getMessage());
        
        // Garante que o banco de dados NUNCA foi chamado neste cenário
        Mockito.verify(repositoryMock, Mockito.never()).adicionarTarefa(anyInt(), anyString());
    }

    @Test
    @DisplayName("TU-03: Deve lançar exceção quando a descrição passar de 255 caracteres")
    void deveLancarExcecaoQuandoDescricaoMuitoGrande() {
        // Cria uma string com 256 caracteres
        String descricaoGigante = "A".repeat(256);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tarefaService.adicionarTarefa(1, descricaoGigante)
        );

        assertEquals("A descrição deve ter no máximo 255 caracteres", exception.getMessage());
    }
}