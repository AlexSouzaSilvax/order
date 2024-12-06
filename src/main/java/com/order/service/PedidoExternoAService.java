package com.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.order.model.PedidoExternoA;

@Service
public class PedidoExternoAService {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String ORDER_EXTERNO_A_URL = System.getenv("ORDER_EXTERNO_A_URL");

    /**
     * Obtém os pedidos da API externa. A requisição é feita utilizando o
     * RestTemplate.
     * A resposta da API é convertida para uma lista de objetos
     * {@link PedidoExternoA}.
     *
     * @return Uma lista de objetos {@link PedidoExternoA}.
     *         Retorna uma lista vazia caso a API não retorne resultados ou em caso
     *         de falha na requisição.
     */
    private List<PedidoExternoA> obterPedidosExternos() {
        PedidoExternoA[] pedidosExternos = restTemplate.getForObject(ORDER_EXTERNO_A_URL, PedidoExternoA[].class);
        return pedidosExternos != null ? List.of(pedidosExternos) : List.of();
    }

    /**
     * Importa os pedidos obtidos da API externa e os processa.
     * Esse método converte os dados recebidos da API externa para o formato
     * adequado e os envia para o serviço
     * {@link PedidoService} para processamento e armazenamento no banco de dados.
     */
    public void importarPedidosExternoA() {
        List<PedidoExternoA> pedidosExternos = obterPedidosExternos();
        if (!pedidosExternos.isEmpty()) {
            pedidoService.processarPedidosExternos(pedidosExternos);
        }
    }
}
