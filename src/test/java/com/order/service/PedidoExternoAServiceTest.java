package com.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.order.model.Pedido;
import com.order.model.PedidoExternoA;

class PedidoExternoAServiceTest {

    @InjectMocks
    private PedidoExternoAService pedidoExternoAService;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa a importação de pedidos externos, verificando se o processo de
     * importação
     * e o processamento de pedidos externos foi chamado corretamente.
     */
    @Test
    void testImportarPedidosExternoA() {
        PedidoExternoA pedidoExternoA1 = new PedidoExternoA("12345", List.of());
        PedidoExternoA pedidoExternoA2 = new PedidoExternoA("67890", List.of());

        when(restTemplate.getForObject(anyString(), eq(PedidoExternoA[].class)))
                .thenReturn(new PedidoExternoA[] { pedidoExternoA1, pedidoExternoA2 });

        pedidoExternoAService.importarPedidosExternoA();

        verify(pedidoService, times(2)).processarPedidosExternos(anyList()); // Verifica se o método foi chamado para os
                                                                             // dois pedidos
    }

    /**
     * Testa a criação de pedidos com um grande volume de dados para simular um
     * cenário
     * com alto número de requisições.
     */
    @Test
    void testCriarPedidosGrandeVolume() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            pedidoService.save(new Pedido()); // Simula a criação de 1000 pedidos
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertTrue(duration < 2000, "A criação de 1000 pedidos não pode demorar mais que 2 segundos");
    }
}
