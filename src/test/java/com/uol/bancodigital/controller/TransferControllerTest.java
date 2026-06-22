package com.uol.bancodigital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uol.bancodigital.dto.TransferRequest;
import com.uol.bancodigital.dto.TransferResponse;
import com.uol.bancodigital.exception.InsufficientFundsException;
import com.uol.bancodigital.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void transferir_deveRetornar201_quandoValido() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"));
        TransferResponse response = new TransferResponse(
                1L, 1L, 2L, new BigDecimal("100.00"), LocalDateTime.now());

        when(transferService.transferir(any(TransferRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transacaoId").value(1));
    }

    @Test
    void transferir_deveRetornar400_quandoSaldoInsuficiente() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"));

        when(transferService.transferir(any(TransferRequest.class)))
                .thenThrow(new InsufficientFundsException());

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferir_deveRetornar400_quandoValorNulo() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, null);

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarTransacoes_deveRetornar200() throws Exception {
        when(transferService.buscarTransacoesPorContaId(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/contas/1/transacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
