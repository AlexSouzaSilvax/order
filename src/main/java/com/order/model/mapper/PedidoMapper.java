package com.order.model.mapper;

import com.order.model.Pedido;
import com.order.model.PedidoExternoA;

public class PedidoMapper {

    public static Pedido fromPedidoExternoA(PedidoExternoA pedidoExternoA) {
        Pedido pedido = new Pedido();
        pedido.setId(pedidoExternoA.getId());
        pedido.setNumeroPedido(pedidoExternoA.getNumeroPedido());
        pedido.setDescontoPercentual(pedidoExternoA.getDescontoPercentual());
        return pedido;
    }
}
