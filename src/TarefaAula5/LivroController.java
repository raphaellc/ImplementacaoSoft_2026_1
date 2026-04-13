package TarefaAula5;

public class LivroController {
    private final LivroRepository repository;
    private final LivroView view;
    private boolean executando;

    public LivroController(LivroRepository repository, LivroView view) {
        this.repository = repository;
        this.view = view;
        this.executando = true;
    }

    public void iniciar() {
        while (executando) {
            String opcao = view.menu();
            switch (opcao) {
                case "1" -> adicionarLivro();
                case "2" -> alugarLivro();
                case "3" -> devolverLivro();
                case "4" -> listarLivros();
                case "5" -> sair();
                default -> view.exibirMensagem("Opção inválida!");
            }
        }
    }

    private void adicionarLivro() {
        String titulo = view.capturarTitulo();
        if (!titulo.isBlank()) {
            repository.AdicionarLivro(titulo);
            view.exibirMensagem("Livro adicionado com sucesso!");
        } else {
            view.exibirMensagem("O título não pode ser vazio.");
        }
    }

    private void alugarLivro() {
        int id = view.capturarId("alugar");
        if (id == -1) {
            view.exibirMensagem("ID inválido. Por favor, digite um número inteiro.");
            return;
        }
        if (repository.marcarComoAlugado(id)) {
            view.exibirMensagem("Livro alugado com sucesso!");
        } else {
            view.exibirMensagem("Erro: Livro não encontrado ou já está alugado.");
        }
    }

    private void devolverLivro() {
        int id = view.capturarId("devolver");
        if (id == -1) {
            view.exibirMensagem("ID inválido. Por favor, digite um número inteiro.");
            return;
        }
        if (repository.marcarComoDisponivel(id)) {
            view.exibirMensagem("Livro devolvido com sucesso!");
        } else {
            view.exibirMensagem("Erro: Livro não encontrado ou já está na biblioteca.");
        }
    }

    private void listarLivros() {
        view.exibirLivros(repository.listarLivros());
    }

    private void sair() {
        this.executando = false;
        view.exibirMensagem("Sistema encerrado.");
    }
}