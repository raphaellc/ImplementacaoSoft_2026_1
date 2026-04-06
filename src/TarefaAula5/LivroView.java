package TarefaAula5;

import java.util.List;
import java.util.Scanner;

public class LivroView {
    private final Scanner s = new Scanner(System.in);

    public String menu() {
        System.out.println("1- Adicionar Livro");
        System.out.println("2- Alugar Livro");
        System.out.println("3- Listar Livros");
        System.out.println("4- Sair");
        System.out.println("Digite uma opcao: ");
        return s.nextLine();
    }

    public String capturarEntrada() {
        return s.nextLine();
    }

    public String capturarTitulo() {
        System.out.print("Digite o título do livro: ");
        return s.nextLine();
    }

    public int capturarId() {
        System.out.print("Digite o ID do livro a marcar como alugado: ");
        try {
            return Integer.parseInt(s.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void exibirLivros(List<Livro> livros) {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }
        System.out.println("Lista Livros: ");
    for (Livro livro : livros) {
        System.out.println(
                livro.id() + " - " + livro.Titulo() +
                        (livro.Alugado() ? " [✔]" : " [] ")
        );
    }

    }
    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

}