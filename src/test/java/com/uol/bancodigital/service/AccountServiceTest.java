package com.uol.bancodigital.service;

import com.uol.bancodigital.domain.Account;
import com.uol.bancodigital.dto.AccountRequest;
import com.uol.bancodigital.dto.AccountResponse;
import com.uol.bancodigital.exception.AccountNotFoundException;
import com.uol.bancodigital.repository.AccountRepository;
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
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void criar_deveRetornarAccountResponse() {
        AccountRequest request = new AccountRequest("Leandro", new BigDecimal("1000.00"));
        Account salvo = new Account("Leandro", new BigDecimal("1000.00"));
        salvo.setId(1L);

        when(accountRepository.save(any(Account.class))).thenReturn(salvo);

        AccountResponse response = accountService.criar(request);

        assertEquals(1L, response.getId());
        assertEquals("Leandro", response.getNome());
        assertEquals(new BigDecimal("1000.00"), response.getSaldo());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void listarTodas_deveRetornarTodasAsContas() {
        Account a1 = new Account("Leandro", new BigDecimal("1000.00"));
        a1.setId(1L);
        Account a2 = new Account("Mariana", new BigDecimal("2000.00"));
        a2.setId(2L);

        when(accountRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        List<AccountResponse> resultado = accountService.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals("Leandro", resultado.get(0).getNome());
        assertEquals("Mariana", resultado.get(1).getNome());
    }

    @Test
    void buscarPorId_deveRetornarConta_quandoExiste() {
        Account account = new Account("Leandro", new BigDecimal("1000.00"));
        account.setId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.buscarPorId(1L);

        assertEquals(1L, response.getId());
        assertEquals("Leandro", response.getNome());
    }

    @Test
    void buscarPorId_deveLancarExcecao_quandoNaoExiste() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.buscarPorId(99L));
    }
}
