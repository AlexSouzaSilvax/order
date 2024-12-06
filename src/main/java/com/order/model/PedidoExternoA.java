package com.order.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PedidoExternoA {

    private Long id;
    private String numeroPedido;
    private Double valor;
    private Double descontoPercentual;
    private LocalDateTime dataCadastro = LocalDateTime.now();
    private List<Produto> produtos = new ArrayList<>();

    public PedidoExternoA(String numeroPedido, List<Produto> listaProdutos) {
        this.numeroPedido = numeroPedido;
        this.produtos = listaProdutos;
    }
}