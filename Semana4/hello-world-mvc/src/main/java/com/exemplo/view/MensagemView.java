package com.exemplo.view;

import com.exemplo.model.Mensagem;

/**
 * View: Responsável pela exibição dos dados ao usuário.
 */
public class MensagemView {
    public void exibirMensagem(Mensagem mensagem) {
        System.out.println("----------------------------------");
        System.out.println("Mensagem do Sistema: " + mensagem.conteudo());
        System.out.println("----------------------------------");
    }
}
