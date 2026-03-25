package br.edu.projeto.controller;
 
import br.edu.projeto.model.Tarefa;
import br.edu.projeto.model.TarefaRepositorio;
import br.edu.projeto.view.TarefaView;
 
import java.util.Optional;
 
public class TarefaController {
 
    private final TarefaRepositorio repositorio;
    private final TarefaView view;
    private boolean rodando;
 
    public TarefaController(TarefaRepositorio repositorio, TarefaView view) {
        this.repositorio = repositorio;
        this.view = view;
        this.rodando = true;
    }
 
  
    public void iniciar() {
        while (rodando) {
            String opcao = view.exibirMenuEObterOpcao();
            processarOpcao(opcao);
        }
        view.exibirDespedida();
        view.fechar();
    }
 
  
    private void processarOpcao(String opcao) {
    
        switch (opcao) {
            case "1" -> adicionarTarefa();
            case "2" -> listarTarefas();
            case "3" -> concluirTarefa();
            case "0" -> rodando = false;
         
            case String s when s.isBlank() ->
                view.exibirErro("Nenhuma opção digitada. Tente novamente.");
            default ->
                view.exibirErro("Opção inválida: \"" + opcao + "\". Escolha entre 0 e 3.");
        }
    }
 

    private void adicionarTarefa() {
        String descricao = view.solicitarDescricaoTarefa();
 
        if (descricao.isBlank()) {
            view.exibirErro("A descrição não pode ser vazia.");
            return;
        }
 
        try {
            Tarefa novaTarefa = repositorio.adicionar(descricao);
            view.exibirTarefaAdicionada(novaTarefa);
        } catch (IllegalArgumentException e) {
            view.exibirErro(e.getMessage());
        }
    }
 
  
    private void listarTarefas() {
        view.exibirListaDeTarefas(repositorio.listarTodas());
    }
 

    private void concluirTarefa() {
        if (repositorio.estaVazia()) {
            view.exibirMensagem("Nenhuma tarefa cadastrada para concluir.");
            return;
        }
 
        listarTarefas();
 
        String entrada = view.solicitarIdTarefa();
 
        int id;
        try {
            id = Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            view.exibirErro("ID inválido: \"" + entrada + "\". Digite apenas números.");
            return;
        }
 
        Optional<Tarefa> tarefaAtualizada = repositorio.concluir(id);
 
        tarefaAtualizada.ifPresentOrElse(
            view::exibirTarefaConcluida,
            () -> view.exibirErro("Nenhuma tarefa encontrada com o ID " + id + ".")
        );
    }
}