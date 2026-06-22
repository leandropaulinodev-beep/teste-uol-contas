package com.uol.bancodigital.service;

import com.uol.bancodigital.domain.Account;
import com.uol.bancodigital.domain.Transaction;
import com.uol.bancodigital.dto.TransferRequest;
import com.uol.bancodigital.dto.TransferResponse;
import com.uol.bancodigital.exception.AccountNotFoundException;
import com.uol.bancodigital.exception.InsufficientFundsException;
import com.uol.bancodigital.repository.AccountRepository;
import com.uol.bancodigital.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    @Transactional
    public TransferResponse transferir(TransferRequest request) {
        if (request.getContaOrigemId().equals(request.getContaDestinoId())) {
            throw new IllegalArgumentException("Conta de origem e destino não podem ser iguais");
        }

        // lock em ordem crescente de id pra não dar deadlock
        Long primeiroId = Math.min(request.getContaOrigemId(), request.getContaDestinoId());
        Long segundoId = Math.max(request.getContaOrigemId(), request.getContaDestinoId());

        var primeira = accountRepository.findByIdForUpdate(primeiroId)
                .orElseThrow(() -> new AccountNotFoundException(primeiroId));
        var segunda = accountRepository.findByIdForUpdate(segundoId)
                .orElseThrow(() -> new AccountNotFoundException(segundoId));

        Account origem = primeiroId.equals(request.getContaOrigemId()) ? primeira : segunda;
        Account destino = primeiroId.equals(request.getContaOrigemId()) ? segunda : primeira;

        if (origem.getSaldo().compareTo(request.getValor()) < 0) {
            throw new InsufficientFundsException();
        }

        origem.setSaldo(origem.getSaldo().subtract(request.getValor()));
        destino.setSaldo(destino.getSaldo().add(request.getValor()));
        accountRepository.save(origem);
        accountRepository.save(destino);

        var transacao = transactionRepository.save(
                new Transaction(request.getContaOrigemId(), request.getContaDestinoId(), request.getValor())
        );

        // notifica ambas as partes (async, não bloqueia)
        notificationService.enviar(origem.getId(),
                "Transferência de R$ %s enviada para conta %d".formatted(request.getValor(), destino.getId()));
        notificationService.enviar(destino.getId(),
                "Transferência de R$ %s recebida da conta %d".formatted(request.getValor(), origem.getId()));

        return new TransferResponse(
                transacao.getId(), transacao.getContaOrigemId(),
                transacao.getContaDestinoId(), transacao.getValor(), transacao.getDataHora()
        );
    }

    public List<Transaction> buscarTransacoesPorContaId(Long contaId) {
        if (!accountRepository.existsById(contaId)) {
            throw new AccountNotFoundException(contaId);
        }
        return transactionRepository.findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(contaId, contaId);
    }
}
