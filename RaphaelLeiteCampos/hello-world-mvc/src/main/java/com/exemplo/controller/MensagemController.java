package com.exemplo.controller;

import com.exemplo.model.Mensagem;
import com.exemplo.view.MensagemView;

/**
 * Controller: Intermediário entre o Model e a View.
 */
public class MensagemController {
    private Mensagem model;
    private MensagemView view;

    public MensagemController(Mensagem model, MensagemView view) {
        this.model = model;
        this.view = view;
    }

    public void atualizarView() {
        view.exibirMensagem(model);
    }

    public void setMensagem(String novoConteudo) {
        this.model = new Mensagem(novoConteudo);
    }
}
