package com.gerenciadortarefas.view;

import com.gerenciadortarefas.model.*;
import java.util.List;
import java.util.Scanner;

public class TarefaView {
    private Scanner scanner;

    public TarefaView(){
        this.scanner = new Scanner(System.in);
    }
    
    public int montarMenu(){
        System.out.println("Escolha uma opção:");
        System.out.println("0. Sair");
        System.out.println("1. Adicionar tarefa");
        System.out.println("2. Listar tarefas");
        System.out.println("3. Marcar tarefa como concluída");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        return opcao;

    }
    public String adicionarTarefa(){
        System.out.println("Digite a descrição da tarefa:");
        String descricao = scanner.nextLine();
        return descricao;
    }

    public void listarTarefas(List<Tarefa> tarefas){
        if (tarefas.isEmpty()){
            System.out.println("Nenhuma tarefa encontrada.");
            return;
        }

        for(Tarefa tarefa : tarefas){
            System.out.println(tarefa.toString());
        }
    }
    public int marcarTarefaConcluida(){
        System.out.println("Digite o ID da tarefa a ser marcada como concluída:");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        return id;
    }
    public void exibirMensagem(String mensagem){
        System.out.println(mensagem);
    }

}
