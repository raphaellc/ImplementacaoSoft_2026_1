package br.edu.projeto.controller;

import br.edu.projeto.model.Livro;
import br.edu.projeto.model.LivroRepository;
import br.edu.projeto.view.LivroView;

import java.util.List;
import java.util.Optional;

public class LivroController {
    private final LivroView livroView;
    private final LivroRepository livroRepository;

    public LivroController(LivroView view, LivroRepository repository) {
        this.livroView = view;
        this.livroRepository = repository;
    }

    public void iniciarGerenciadorLivros() {
        int opcao;
        do {
            opcao = livroView.montarMenu();
            switch (opcao) {
                case 1 -> addBook();
                case 2 -> listBooks();
                case 3 -> updateBook();
                case 4 -> deleteBook();
                case 0 -> livroView.exibirMensagem("Saindo do sistema.");
                default -> livroView.exibirMensagem("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }

       public void addBook() {
        // View valida os dados antes de enviar ao Controller
        String[] dados = livroView.solicitarDadosLivro();
        String titulo = dados[0];
        String autor  = dados[1];

        int idGerado = livroRepository.addBook(titulo, autor);

        if (idGerado > 0) {
            livroView.exibirMensagem("Livro adicionado com sucesso! ID: " + idGerado);
            listBooks();
        } else {
            livroView.exibirMensagem("Erro ao adicionar o livro.");
        }
    }

  
    public void listBooks() {
        List<Livro> livros = livroRepository.listBooks();
        livroView.listarLivros(livros);
    }

 
    public void updateBook() {
        int id = livroView.solicitarId("atualizar");

        // Busca o livro — notFound tratado aqui no Controller 
        Optional<Livro> livroOptional = livroRepository.findById(id);
        if (livroOptional.isEmpty()) {
            livroView.exibirMensagem("Livro com ID " + id + " não encontrado.");
            return;
        }

        Livro livroAtual = livroOptional.get();
        String[] alteracoes = livroView.solicitarAtualizacoes(livroAtual);

        Livro livroAtualizado = livroAtual
                .comTitulo(alteracoes[0])
                .comAutor(alteracoes[1])
                .comIsbn(alteracoes[2])
                .comDisponivel(alteracoes[3].equals("s"));

        if (livroRepository.updateBook(livroAtualizado)) {
            livroView.exibirMensagem("Livro atualizado com sucesso!");
            listBooks();
        } else {
            livroView.exibirMensagem("Erro ao atualizar o livro.");
        }
    }


    public void deleteBook() {
        int id = livroView.solicitarId("remover");

                Optional<Livro> livroOptional = livroRepository.findById(id);
        if (livroOptional.isEmpty()) {
            livroView.exibirMensagem("Livro com ID " + id + " não encontrado.");
            return;
        }

        if (livroRepository.deleteBook(id)) {
            livroView.exibirMensagem("Livro com ID " + id + " removido com sucesso!");
            listBooks();
        } else {
            livroView.exibirMensagem("Erro ao remover o livro.");
        }
    }
}