package com.uol.bancodigital.service;

import com.uol.bancodigital.domain.Account;
import com.uol.bancodigital.dto.AccountRequest;
import com.uol.bancodigital.dto.AccountResponse;
import com.uol.bancodigital.exception.AccountNotFoundException;
import com.uol.bancodigital.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse criar(AccountRequest request) {
        var account = new Account(request.getNome(), request.getSaldo());
        var salvo = accountRepository.save(account);
        return toResponse(salvo);
    }

    public List<AccountResponse> listarTodas() {
        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse buscarPorId(Long id) {
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return toResponse(account);
    }

    private AccountResponse toResponse(Account acc) {
        return new AccountResponse(acc.getId(), acc.getNome(), acc.getSaldo());
    }
}
