package com.gerenciadortarefas;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfiguracaoTest {

    @Test
    void testarSeJUnitEstaRodando() {
        // Arrange & Act
        int soma = 2 + 2;
        
        // Assert: Se isso passar, o JUnit 5 está instalado e funcionando!
        assertEquals(4, soma, "A matemática básica deve funcionar e o JUnit também!");
    }

    @Test
    void testarSeMockitoEstaRodando() {
        // Arrange: Criamos um "mock" (imitação) de uma interface nativa do Java
        List<String> listaMock = Mockito.mock(List.class);

        // Ensinamos o mock a retornar um valor específico quando chamarem o método size()
        Mockito.when(listaMock.size()).thenReturn(100);

        // Act & Assert: Se isso passar, o Mockito está instalado e conseguindo criar mocks!
        assertEquals(100, listaMock.size(), "O Mockito deve conseguir simular o comportamento da lista.");
    }
}
