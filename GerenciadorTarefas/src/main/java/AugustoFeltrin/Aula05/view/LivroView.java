package AugustoFeltrin.Aula05.view;

import java.util.List;
import java.util.Scanner;
import AugustoFeltrin.Aula05.model.Livro;

public class LivroView {
    private Scanner scan = new Scanner(System.in);

    public String menu(){
        System.out.println("\n--- Lista de Leituras ---");
        System.out.println("1. Adicionar livro");
        System.out.println("2. Listar livros");
        System.out.println("3. Marcar livro como lido");
        System.out.println("4. Remover livro");
        System.out.println("5. Sair");
        System.out.print("Escolha: ");
        return scan.nextLine();
    }

    public void exibirLivros(List<Livro> livros){
        if(livros.isEmpty()){
            System.out.println("Nenhum livro adicionado!");
        } else {
            for(Livro L : livros){
                String status;
                if(L.concluida()){
                    status = "Lido";
                } else {
                    status = "Não lido";
                }
                System.out.println("[" + L.id() + "] " + L.titulo() + " (" + status + ")");
            }
        }
    }

    public String pedirTitulo(){
        System.out.print("Título do livro: ");
        return scan.nextLine();
    }

    public int pedirID(){
        System.out.print("ID do livro: ");
        try{
            return Integer.parseInt(scan.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}
