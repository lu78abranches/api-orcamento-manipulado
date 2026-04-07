package com.farmacia.api_orcamento_manipulado.model;

public class Orcamento {

    /* Criamos o método, mas retornamos null ou vazio para o teste falhar no valor
    public String getStatus() {
        return null;
    }*/
    // 1. Definimos o valor que o teste espera
    private String status = "PENDENTE_REVISAO";

    // 2. Retornamos esse valor no método
    public String getStatus() {
        return this.status;
    }
}
