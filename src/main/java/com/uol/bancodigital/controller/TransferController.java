package com.uol.bancodigital.controller;

import com.uol.bancodigital.domain.Transaction;
import com.uol.bancodigital.dto.TransferRequest;
import com.uol.bancodigital.dto.TransferResponse;
import com.uol.bancodigital.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Transferências", description = "Transferências e movimentações")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/transferencias")
    @Operation(summary = "Realizar transferência entre contas")
    public ResponseEntity<TransferResponse> transferir(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.transferir(request));
    }

    @GetMapping("/contas/{id}/transacoes")
    @Operation(summary = "Consultar movimentações de uma conta")
    public ResponseEntity<List<Transaction>> buscarTransacoes(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.buscarTransacoesPorContaId(id));
    }
}
