package com.order.controller;

import com.order.service.PedidoExternoAService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/externo-a")
public class PedidoExternoAController {

    private final PedidoExternoAService pedidoExternoAService;

    public PedidoExternoAController(PedidoExternoAService pedidoExternoAService) {
        this.pedidoExternoAService = pedidoExternoAService;
    }

    @PostMapping("/pedidos/importar")
    public ResponseEntity<Void> importarPedidosExternosA() {
        pedidoExternoAService.importarPedidosExternoA();
        return ResponseEntity.ok().build();
    }
}
