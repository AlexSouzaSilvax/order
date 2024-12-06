package com.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}