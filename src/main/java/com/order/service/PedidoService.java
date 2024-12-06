package com.order.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.order.model.Pedido;
import com.order.model.PedidoExternoA;
import com.order.model.PedidoProduto;
import com.order.model.Produto;
import com.order.model.mapper.PedidoMapper;
import com.order.repository.PedidoRepository;
import com.order.repository.ProdutoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    /**
     * Retorna todos os pedidos, incluindo seus produtos associados.
     * 
     * @return Lista de pedidos com seus produtos
     */
    public Page<Pedido> findAll(Pageable pageable) {
        return pedidoRepository.findAllPedidosComProdutos(pageable);
    }

    /**
     * Processa uma lista de pedidos externos, validando e criando novos pedidos.
     * 
     * @param pedidosExternos Lista de pedidos externos a serem processados
     */
    @Transactional
    @Async
    public void processarPedidosExternos(List<PedidoExternoA> pedidosExternos) {
        pedidosExternos.forEach(pedidoExternoA -> {

            if (!existePedidoByNumero(pedidoExternoA.getNumeroPedido())) {
                Pedido pedido = PedidoMapper.fromPedidoExternoA(pedidoExternoA);

                List<Produto> produtosSalvos = produtoRepository.saveAll(pedidoExternoA.getProdutos());

                List<PedidoProduto> pedidoProdutos = produtosSalvos.stream()
                        .map(produto -> criarPedidoProduto(pedido, produto, 1))
                        .collect(Collectors.toList());

                pedido.setPedidoProdutos(pedidoProdutos);
                pedido.setValor(calcularValorTotal(pedidoProdutos));

                save(pedido);

                System.out.println("Importando pedidos do Externo A...");

                // Envia uma mensagem para o Kafka após a importação
                // kafkaProducer.sendMensagemImportacao("Pedidos do Externo A importados com
                // sucesso!");

            }
        });
    }

    /**
     * Salva um pedido no banco de dados.
     * 
     * @param pedido O pedido a ser salvo
     * @return O pedido salvo
     */
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    /**
     * Verifica se um pedido com o número fornecido já existe no banco de dados.
     * 
     * @param numeroPedido O número do pedido a ser verificado
     * @return true se o pedido já existir, false caso contrário
     */
    public Boolean existePedidoByNumero(String numeroPedido) {
        return pedidoRepository.existsByNumeroPedido(numeroPedido);
    }

    /**
     * Retorna um pedido pelo seu ID.
     * 
     * @param id O ID do pedido
     * @return O pedido correspondente ao ID, se existir
     */
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    /**
     * Retorna um pedido pelo seu número de pedido.
     * 
     * @param numeroPedido O número do pedido
     * @return O pedido correspondente ao número, se existir
     */
    public Optional<Pedido> findByNumeroPedido(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido);
    }

    // Métodos Auxiliares

    /**
     * Cria um novo pedido produto associado ao pedido e produto fornecidos.
     * 
     * @param pedido     O pedido ao qual o produto será associado
     * @param produto    O produto a ser associado
     * @param quantidade A quantidade do produto
     * @return O PedidoProduto criado
     */
    private PedidoProduto criarPedidoProduto(Pedido pedido, Produto produto, int quantidade) {
        PedidoProduto pedidoProduto = new PedidoProduto();
        pedidoProduto.setPedido(pedido);
        pedidoProduto.setProduto(produto);
        pedidoProduto.setQuantidade(quantidade);
        return pedidoProduto;
    }

    /**
     * Calcula o valor total de um pedido a partir dos produtos associados.
     * 
     * @param pedidoProdutos A lista de produtos associados ao pedido
     * @return O valor total do pedido
     */
    private Double calcularValorTotal(List<PedidoProduto> pedidoProdutos) {
        double valorTotal = pedidoProdutos.stream()
                .mapToDouble(p -> p.getProduto().getValor() * p.getQuantidade())
                .sum();
        return valorTotal;
    }
}
