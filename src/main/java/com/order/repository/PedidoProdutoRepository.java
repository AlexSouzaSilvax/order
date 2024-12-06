package com.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order.model.PedidoProduto;

public interface PedidoProdutoRepository extends JpaRepository<PedidoProduto, Long> {
}