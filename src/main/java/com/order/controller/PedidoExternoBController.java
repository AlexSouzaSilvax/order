package com.order.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.order.exception.ResourceNotFoundException;
import com.order.model.Pedido;
import com.order.service.PedidoService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("/api/externo-b")
public class PedidoExternoBController {

    private final PedidoService pedidoService;

    public PedidoExternoBController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/pedidos")
    @RateLimiter(name = "pedidosRateLimiter", fallbackMethod = "rateLimitExceeded")
    public ResponseEntity<Page<Pedido>> findAll(@RequestParam int pagina, @RequestParam int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        return (ResponseEntity<Page<Pedido>>) pedidoService.findAll(pageable);

        
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

    public List<Pedido> rateLimitExceeded(Throwable t) {
    throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Limite de requisições excedido.");
}
}
