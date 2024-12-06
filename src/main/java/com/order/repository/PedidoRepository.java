package com.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.order.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca todos os pedidos com seus produtos associados, usando `LEFT JOIN FETCH` para evitar problemas de `LazyInitializationException`.
     * @return Lista de pedidos com produtos carregados.
     */
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.pedidoProdutos")
    Page<Pedido> findAllPedidosComProdutos(Pageable pageable);

    /**
     * Verifica se existe um pedido com o número especificado.
     * Essa consulta é otimizada para retornar apenas um booleano.
     * 
     * @param numeroPedido Número do pedido a ser verificado.
     * @return true se o pedido existir, false caso contrário.
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Pedido p WHERE p.numeroPedido = :numeroPedido")
    boolean existsByNumeroPedido(@Param("numeroPedido") String numeroPedido);

    /**
     * Busca um pedido com base no número do pedido.
     * 
     * @param numeroPedido Número do pedido a ser buscado.
     * @return Um Optional contendo o pedido, caso exista.
     */
    Optional<Pedido> findByNumeroPedido(@Param("numeroPedido") String numeroPedido);

    /**
     * Consulta todos os pedidos com base em um intervalo de IDs.
     * Exemplo de consulta adicional útil para otimização.
     * 
     * @param startId ID inicial.
     * @param endId ID final.
     * @return Lista de pedidos no intervalo especificado.
     */
    @Query("SELECT p FROM Pedido p WHERE p.id BETWEEN :startId AND :endId")
    List<Pedido> findPedidosByIdRange(@Param("startId") Long startId, @Param("endId") Long endId);

}
