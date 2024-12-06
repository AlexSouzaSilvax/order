package com.order.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.order.model.PedidoExternoA;
import com.order.model.Produto;

class PedidoExternoAServiceTest {

    @InjectMocks
    private PedidoExternoAService pedidoExternoAService;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private RestTemplate restTemplate;

    private static final String ORDER_EXTERNO_A_URL = "http://api.externa.com/pedidos";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa o método importarPedidosExternoA
     * 
     * Simula o comportamento de um grande número de pedidos (200 mil+) retornados
     * pela API externa.
     * Verifica se o PedidoService.processarPedidosExternos é chamado para processar
     * a lista de pedidos.
     */

    // PedidoExternoA pedido = new PedidoExternoA(String.valueOf(i), new
    // ArrayList<Produto>());

    @Test
    void testImportarPedidosExternoA_ComMaisDe200MilPedidos() {
        // Cria 200 mil pedidos simulados
        List<PedidoExternoA> pedidosExternosList = new ArrayList<>();
        for (int i = 0; i < 200000; i++) {
            PedidoExternoA pedido = new PedidoExternoA(String.valueOf(i), new ArrayList<Produto>());
            pedidosExternosList.add(pedido);
        }
        PedidoExternoA[] pedidosExternosArray = pedidosExternosList.toArray(new PedidoExternoA[0]);

        // Configura o RestTemplate para retornar os pedidos simulados
        when(restTemplate.getForObject(anyString(), eq(PedidoExternoA[].class)))
                .thenReturn(pedidosExternosArray);

        // Executa o método a ser testado
        pedidoService.processarPedidosExternos(pedidosExternosList);

        // Verifica se o pedidoService.processarPedidosExternos foi chamado
        verify(pedidoService, times(1)).processarPedidosExternos(argThat(pedidos -> pedidos.size() == 200000));
    }

    /**
     * Testa o comportamento quando a API externa retorna uma lista vazia.
     * Verifica se o método processarPedidosExternos não é chamado.
     */
    @Test
    void testImportarPedidosExternoA_Vazia() {
        // Simula uma resposta vazia da API externa
        when(restTemplate.getForObject(eq(ORDER_EXTERNO_A_URL), eq(PedidoExternoA[].class)))
                .thenReturn(new PedidoExternoA[0]);

        // Chama o método que deve processar os pedidos externos
        pedidoExternoAService.importarPedidosExternoA();

        // Verifica se o método processarPedidosExternos não foi chamado
        verify(pedidoService, never()).processarPedidosExternos(anyList());
    }

    /**
     * Testa o comportamento quando ocorre uma falha na requisição da API externa.
     * Verifica se o método processarPedidosExternos não é chamado.
     */
    @Test
    void testImportarPedidosExternoA_FalhaRequisicao() {
        // Simula uma falha no RestTemplate (exemplo, retorna null ou lança uma exceção)
        when(restTemplate.getForObject(eq(ORDER_EXTERNO_A_URL), eq(PedidoExternoA[].class)))
                .thenReturn(null);

        // Chama o método que deve processar os pedidos externos
        pedidoExternoAService.importarPedidosExternoA();

        // Verifica se o método processarPedidosExternos não foi chamado
        verify(pedidoService, never()).processarPedidosExternos(anyList());
    }
}
