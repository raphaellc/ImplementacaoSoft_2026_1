package TarefaAula5;
import TarefaAula5.LivroView;
import TarefaAula5.LivroRepository;
import TarefaAula5.Livro;

public class LivroController {
    private LivroRepository repository;
    private LivroView view;
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
                case "1" -> AdicionarLivro();
                case "2" -> marcarComoAlugado();
                case "3" -> listarLivros();
                case "4" -> Sair();

            }
        }
    }

    private void AdicionarLivro() {
        String titulo = view.capturarTitulo();
        if (!titulo.isBlank()) {
            repository.AdicionarLivro(titulo);
            view.exibirMensagem("Livro adicionado com sucesso!");
        } else {
            view.exibirMensagem("O título não pode ser vazio.");
        }
    }

    private void listarLivros() {
        view.exibirLivros(repository.listarLivros());
    }

    private void marcarComoAlugado() {
        int id = view.capturarId();
        if (id == -1) {
            view.exibirMensagem("ID inválido. Por favor, digite um número inteiro.");
            return;
        }

        boolean sucesso = repository.marcarComoAlugado(id);
        if (sucesso) {
            view.exibirMensagem("Livro atualizado e marcado como lido!");
        } else {
            view.exibirMensagem("Erro: Nenhum livro encontrado com o ID fornecido.");
        }

    }
    public void Sair() {
        executando = false;
    }
}
