package com.uol.bancodigital.controller;

import com.uol.bancodigital.dto.AccountRequest;
import com.uol.bancodigital.dto.AccountResponse;
import com.uol.bancodigital.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Gestão de contas bancárias")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Cadastrar nova conta")
    public ResponseEntity<AccountResponse> criar(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas")
    public ResponseEntity<List<AccountResponse>> listarTodas() {
        return ResponseEntity.ok(accountService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar conta por ID")
    public ResponseEntity<AccountResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.buscarPorId(id));
    }
}
