package TarefaAula5;

import java.util.List;
import java.util.Scanner;

public class LivroView {
    private final Scanner s = new Scanner(System.in);

    public String menu() {
        System.out.println("\n--- BIBLIOTECA ---");
        System.out.println("1- Adicionar Livro");
        System.out.println("2- Alugar Livro");
        System.out.println("3- Devolver Livro");
        System.out.println("4- Listar Livros");
        System.out.println("5- Sair");
        System.out.print("Digite uma opção: ");
        return s.nextLine();
    }

    public String capturarTitulo() {
        System.out.print("Digite o título do livro: ");
        return s.nextLine();
    }

    public int capturarId(String acao) {
        System.out.print("Digite o ID do livro para " + acao + ": ");
        try {
            return Integer.parseInt(s.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Retorna -1 se o utilizador digitar algo que não seja um número
        }
    }

    public void exibirLivros(List<Livro> livros) {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }
        System.out.println("\nLista de Livros:");
        for (Livro livro : livros) {
            String status = livro.Alugado() ? "[ALUGADO]" : "[DISPONÍVEL]";
            System.out.println(livro.id() + " - " + livro.Titulo() + " " + status);
        }
    }

    public void exibirMensagem(String msg) {
        System.out.println(">>> " + msg);
    }
}