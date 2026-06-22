package com.uol.bancodigital.repository;

import com.uol.bancodigital.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(
            Long contaOrigemId, Long contaDestinoId);
}
