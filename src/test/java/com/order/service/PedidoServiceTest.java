package com.order.service;

import com.order.model.Pedido;
import com.order.model.PedidoExternoA;
import com.order.model.PedidoProduto;
import com.order.model.Produto;
import com.order.repository.PedidoRepository;
import com.order.repository.ProdutoRepository;
import com.order.model.mapper.PedidoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa a criação de um pedido com produtos e cálculo do valor total.
     * Verifica se os produtos são salvos e se o valor total do pedido é calculado corretamente.
     */
    @Test
    void testCriarPedido() {
        Produto produto1 = new Produto();
        produto1.setNome("Produto 1");
        produto1.setValor(100.0);

        Produto produto2 = new Produto();
        produto2.setNome("Produto 2");
        produto2.setValor(200.0);

        Pedido pedido = new Pedido();
        pedido.setPedidoProdutos(Arrays.asList(
                new PedidoProduto(pedido, produto1, 2),
                new PedidoProduto(pedido, produto2, 3)
        ));
        pedido.setValor(2 * 100.0 + 3 * 200.0); // 2 * 100 + 3 * 200 = 800

        when(produtoRepository.saveAll(anyList())).thenReturn(Arrays.asList(produto1, produto2));

        Pedido pedidoSalvo = pedidoService.save(pedido);

        assertNotNull(pedidoSalvo, "O pedido salvo não pode ser nulo");
        assertEquals(800.0, pedidoSalvo.getValor(), "O valor total do pedido está incorreto");
        verify(produtoRepository, times(2)).save(any(Produto.class));  // Verifica se os produtos foram salvos
    }

    /**
     * Testa o processamento de pedidos externos, verificando se um pedido é processado corretamente
     * e salvado no banco quando não existe.
     */
    @Test
    void testProcessarPedidosExternos() {
        Produto produto = new Produto();
        produto.setNome("Produto 1");
        produto.setValor(100.0);

        PedidoExternoA pedidoExternoA = new PedidoExternoA("12345", List.of(produto));
        Pedido pedido = PedidoMapper.fromPedidoExternoA(pedidoExternoA);
        
        when(pedidoRepository.existsByNumeroPedido("12345")).thenReturn(false);
        when(produtoRepository.saveAll(anyList())).thenReturn(List.of(produto));

        pedidoService.processarPedidosExternos(List.of(pedidoExternoA));

        verify(pedidoRepository, times(1)).save(any(Pedido.class));  // Verifica se o pedido foi salvo
        verify(produtoRepository, times(1)).saveAll(anyList());  // Verifica se os produtos foram salvos
    }

    /**
     * Testa a verificação de existência de um pedido.
     * Verifica se o método existePedidoByNumero retorna verdadeiro ou falso corretamente.
     */
    @Test
    void testExistePedidoByNumero() {
        String numeroPedidoExistente = "12345";
        String numeroPedidoInexistente = "67890";
        
        when(pedidoRepository.existsByNumeroPedido(numeroPedidoExistente)).thenReturn(true);
        when(pedidoRepository.existsByNumeroPedido(numeroPedidoInexistente)).thenReturn(false);

        assertTrue(pedidoService.existePedidoByNumero(numeroPedidoExistente), "O pedido deveria existir");
        assertFalse(pedidoService.existePedidoByNumero(numeroPedidoInexistente), "O pedido não deveria existir");
        
        verify(pedidoRepository, times(2)).existsByNumeroPedido(anyString());
    }

    /**
     * Testa o método findById, verificando se o pedido pode ser encontrado pelo ID.
     */
    @Test
    void testFindById() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Optional<Pedido> pedidoOptional = pedidoService.findById(1L);

        assertTrue(pedidoOptional.isPresent(), "O pedido deveria ser encontrado");
        assertEquals(1L, pedidoOptional.get().getId(), "O ID do pedido encontrado está incorreto");
    }

    /**
     * Testa o método findByNumeroPedido, verificando se um pedido pode ser encontrado pelo número do pedido.
     */
    @Test
    void testFindByNumeroPedido() {
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("12345");
        when(pedidoRepository.findByNumeroPedido("12345")).thenReturn(Optional.of(pedido));

        Optional<Pedido> pedidoOptional = pedidoService.findByNumeroPedido("12345");

        assertTrue(pedidoOptional.isPresent(), "O pedido deveria ser encontrado pelo número");
        assertEquals("12345", pedidoOptional.get().getNumeroPedido(), "O número do pedido encontrado está incorreto");
    }
}
