package com.exemplo.main;

import com.exemplo.model.Mensagem;
import com.exemplo.view.MensagemView;
import com.exemplo.controller.MensagemController;

/**
 * Classe principal que orquestra o Hello World em MVC.
 */
public class MainApp {
    public static void main(String[] args) {
        // 1. Criar o Model (Dados)
        Mensagem model = new Mensagem("Hello World!");

        // 2. Criar a View (Interface)
        MensagemView view = new MensagemView();

        // 3. Criar o Controller (Lógica)
        MensagemController controller = new MensagemController(model, view);

        // 4. Executar a ação de exibição
        controller.atualizarView();

        // 5. Atualizar os dados via Controller e exibir novamente
        controller.setMensagem("Olá Mundo! (MVC com Java 25)");
        controller.atualizarView();
    }
}
