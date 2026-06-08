package com.teste.helloworld.service;

import com.teste.helloworld.repository.SaudacaoRepository;

public class SaudacaoService {

    private final SaudacaoRepository repository;

    // Injeção de dependência via construtor
    public SaudacaoService(SaudacaoRepository repository) {
        this.repository = repository;
    }

    public String gerarHelloWorld(String nome, String idioma) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser vazio");
        }

        // O serviço delega a busca da palavra para o repositório externo
        String saudacao = repository.buscarSaudacaoPorIdioma(idioma);
        
        if (saudacao == null) {
            saudacao = "Hello"; // Fallback padrão
        }

        return saudacao + ", " + nome + "!";
    }
}
