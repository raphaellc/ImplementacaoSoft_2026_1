package com.teste.helloworld.service;


import com.teste.helloworld.repository.SaudacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class SaudacaoServiceTest {

    private SaudacaoRepository repositoryMock;
    private SaudacaoService saudacaoService;

    @BeforeEach
    void setUp() {
        // 1. Criamos a imitação (mock) da interface
        repositoryMock = Mockito.mock(SaudacaoRepository.class);
        
        // 2. Injetamos o mock no serviço real que queremos testar
        saudacaoService = new SaudacaoService(repositoryMock);
    }

    @Test
    @DisplayName("Deve gerar o Hello World com sucesso usando a saudação do repositório")
    void deveGerarHelloWorldComSucesso() {
        // Arrange (Preparação)
        String nome = "Raphael";
        String idioma = "PT";
        
        // Ensinamos o mock: Quando chamarem o método com "PT", responda "Olá"
        Mockito.when(repositoryMock.buscarSaudacaoPorIdioma("PT"))
               .thenReturn("Olá");

        // Act (Ação)
        String resultado = saudacaoService.gerarHelloWorld(nome, idioma);

        // Assert (Verificação)
        assertEquals("Olá, Raphael!", resultado, "A mensagem formatada deve estar correta");
        
        // Verifica se o serviço realmente consultou o repositório 1 vez
        Mockito.verify(repositoryMock, Mockito.times(1)).buscarSaudacaoPorIdioma("PT");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome fornecido for inválido")
    void deveLancarExcecaoQuandoNomeForVazio() {
        // Act & Assert
        IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class,
            () -> saudacaoService.gerarHelloWorld("   ", "EN")
        );

        assertEquals("O nome não pode ser vazio", excecao.getMessage());
        
        // Garante que o repositório NUNCA foi chamado, pois o método barrou na validação inicial
        Mockito.verify(repositoryMock, Mockito.never()).buscarSaudacaoPorIdioma(anyString());
    }
}