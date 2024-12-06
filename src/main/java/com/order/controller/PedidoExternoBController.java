package com.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.exception.ResourceNotFoundException;
import com.order.model.Pedido;
import com.order.service.PedidoService;

@RestController
@RequestMapping("/api/externo-b")
public class PedidoExternoBController {

    private final PedidoService pedidoService;

    public PedidoExternoBController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pedidos")
    public ResponseEntity<List<Pedido>> findAll() {
        List<Pedido> pedidos = pedidoService.findAll();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<Pedido> findById(@PathVariable Long id) {
        Pedido pedido = pedidoService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/pedidos/numero/{numeroPedido}")
    public ResponseEntity<Pedido> findByNumeroPedido(@PathVariable String numeroPedido) {
        Pedido pedido = pedidoService.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com número: " + numeroPedido));
        return ResponseEntity.ok(pedido);
    }
}
