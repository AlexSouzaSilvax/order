package com.order.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.order.model.Pedido;
import com.order.model.PedidoExternoA;
import com.order.model.PedidoProduto;
import com.order.model.Produto;
import com.order.model.mapper.PedidoMapper;
import com.order.repository.PedidoRepository;
import com.order.repository.ProdutoRepository;

import jakarta.transaction.Transactional;

/**
 * Classe de serviço para a manipulação dos pedidos.
 * Contém métodos para criar, processar e consultar pedidos, bem como outros métodos auxiliares.
 */
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository; // Repositório de Pedido para acessar o banco de dados

    @Autowired
    private ProdutoRepository produtoRepository; // Repositório de Produto para acessar o banco de dados

    /**
     * Retorna todos os pedidos, incluindo seus produtos associados.
     * 
     * @return Lista de pedidos com seus produtos
     */
    public List<Pedido> findAll() {
        return pedidoRepository.findAllPedidosComProdutos();
    }

    /**
     * Cria um novo pedido com produtos e um desconto aplicado.
     * 
     * @return Pedido criado e salvo no banco de dados
     */
    @Transactional
    public Pedido criarPedido() {
        // Criação de produtos
        Produto produto1 = criarProduto("Hyundai Creta 2025", 150000.00);
        Produto produto2 = criarProduto("Honda HRV 2018", 98000.00);

        // Lista de produtos para o pedido
        List<Produto> produtos = List.of(produto1, produto2);

        Pedido pedido = new Pedido();
        pedido.setDescontoPercentual(10.0); // 10% de desconto

        // Criando os produtos do pedido
        List<PedidoProduto> pedidoProdutos = produtos.stream()
                .map(produto -> criarPedidoProduto(pedido, produto, 2)) // 2 unidades de cada produto
                .collect(Collectors.toList());

        pedido.setPedidoProdutos(pedidoProdutos); // Associando os produtos ao pedido
        pedido.setValor(calcularValorTotal(pedidoProdutos)); // Calculando o valor total do pedido

        return pedidoRepository.save(pedido); // Salvando o pedido no banco de dados
    }

    /**
     * Processa uma lista de pedidos externos, validando e criando novos pedidos.
     * 
     * @param pedidosExternos Lista de pedidos externos a serem processados
     */
    @Transactional
    public void processarPedidosExternos(List<PedidoExternoA> pedidosExternos) {
        pedidosExternos.forEach(pedidoExternoA -> {
            // Verifica se o pedido já existe no banco de dados para evitar duplicação
            if (!existePedidoByNumero(pedidoExternoA.getNumeroPedido())) {
                Pedido pedido = PedidoMapper.fromPedidoExternoA(pedidoExternoA); // Mapeia o pedido externo para um pedido interno

                // Salvando os produtos associados ao pedido
                List<Produto> produtosSalvos = produtoRepository.saveAll(pedidoExternoA.getProdutos());

                // Criando os produtos do pedido
                List<PedidoProduto> pedidoProdutos = produtosSalvos.stream()
                        .map(produto -> criarPedidoProduto(pedido, produto, 1)) // 1 unidade de cada produto
                        .collect(Collectors.toList());

                pedido.setPedidoProdutos(pedidoProdutos); // Associando os produtos ao pedido
                pedido.setValor(calcularValorTotal(pedidoProdutos)); // Calculando o valor total do pedido

                save(pedido); // Salvando o pedido no banco de dados
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
     * Cria um novo produto e o salva no banco de dados.
     * 
     * @param nome O nome do produto
     * @param valor O valor do produto
     * @return O produto criado e salvo
     */
    private Produto criarProduto(String nome, Double valor) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setValor(valor);
        return produtoRepository.save(produto);
    }

    /**
     * Cria um novo pedido produto associado ao pedido e produto fornecidos.
     * 
     * @param pedido O pedido ao qual o produto será associado
     * @param produto O produto a ser associado
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
