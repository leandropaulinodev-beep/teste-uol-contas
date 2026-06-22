package com.uol.bancodigital.service;

import com.uol.bancodigital.domain.Account;
import com.uol.bancodigital.domain.Transaction;
import com.uol.bancodigital.dto.TransferRequest;
import com.uol.bancodigital.dto.TransferResponse;
import com.uol.bancodigital.exception.AccountNotFoundException;
import com.uol.bancodigital.exception.InsufficientFundsException;
import com.uol.bancodigital.repository.AccountRepository;
import com.uol.bancodigital.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferir_deveTransferirComSucesso_quandoRequestValido() {
        Account origem = new Account("Leandro", new BigDecimal("1000.00"));
        origem.setId(1L);
        Account destino = new Account("Mariana", new BigDecimal("2000.00"));
        destino.setId(2L);

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(origem));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(destino));

        Transaction transacaoSalva = new Transaction(1L, 2L, new BigDecimal("300.00"));
        transacaoSalva.setId(1L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transacaoSalva);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("300.00"));
        TransferResponse response = transferService.transferir(request);

        assertEquals(new BigDecimal("700.00"), origem.getSaldo());
        assertEquals(new BigDecimal("2300.00"), destino.getSaldo());
        assertEquals(1L, response.getTransacaoId());
        verify(notificationService, times(2)).enviar(anyLong(), anyString());
    }

    @Test
    void transferir_deveLancarSaldoInsuficiente_quandoSaldoBaixo() {
        Account origem = new Account("Leandro", new BigDecimal("100.00"));
        origem.setId(1L);
        Account destino = new Account("Mariana", new BigDecimal("2000.00"));
        destino.setId(2L);

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(origem));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(destino));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("500.00"));

        assertThrows(InsufficientFundsException.class, () -> transferService.transferir(request));
    }

    @Test
    void transferir_deveLancarContaNaoEncontrada_quandoOrigemNaoExiste() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"));

        assertThrows(AccountNotFoundException.class, () -> transferService.transferir(request));
    }

    @Test
    void transferir_deveLancarArgumentoInvalido_quandoMesmaConta() {
        TransferRequest request = new TransferRequest(1L, 1L, new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> transferService.transferir(request));
    }

    @Test
    void buscarTransacoes_deveRetornarTransacoes() {
        when(accountRepository.existsById(1L)).thenReturn(true);

        Transaction t1 = new Transaction(1L, 2L, new BigDecimal("100.00"));
        t1.setId(1L);
        when(transactionRepository
                .findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(1L, 1L))
                .thenReturn(Arrays.asList(t1));

        List<Transaction> resultado = transferService.buscarTransacoesPorContaId(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarTransacoes_deveLancarExcecao_quandoContaNaoExiste() {
        when(accountRepository.existsById(99L)).thenReturn(false);

        assertThrows(AccountNotFoundException.class,
                () -> transferService.buscarTransacoesPorContaId(99L));
    }
}
