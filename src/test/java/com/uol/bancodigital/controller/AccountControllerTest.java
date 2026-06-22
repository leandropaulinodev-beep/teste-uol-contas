package com.uol.bancodigital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uol.bancodigital.dto.AccountRequest;
import com.uol.bancodigital.dto.AccountResponse;
import com.uol.bancodigital.exception.AccountNotFoundException;
import com.uol.bancodigital.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criarConta_deveRetornar201() throws Exception {
        AccountRequest request = new AccountRequest("Leandro", new BigDecimal("1000.00"));
        AccountResponse response = new AccountResponse(1L, "Leandro", new BigDecimal("1000.00"));

        when(accountService.criar(any(AccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Leandro"));
    }

    @Test
    void criarConta_deveRetornar400_quandoNomeVazio() throws Exception {
        AccountRequest request = new AccountRequest("", new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodas_deveRetornar200() throws Exception {
        when(accountService.listarTodas()).thenReturn(Arrays.asList(
                new AccountResponse(1L, "Leandro", new BigDecimal("1000.00"))));

        mockMvc.perform(get("/api/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Leandro"));
    }

    @Test
    void buscarPorId_deveRetornar200_quandoExiste() throws Exception {
        when(accountService.buscarPorId(1L))
                .thenReturn(new AccountResponse(1L, "Leandro", new BigDecimal("1000.00")));

        mockMvc.perform(get("/api/contas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Leandro"));
    }

    @Test
    void buscarPorId_deveRetornar404_quandoNaoExiste() throws Exception {
        when(accountService.buscarPorId(99L)).thenThrow(new AccountNotFoundException(99L));

        mockMvc.perform(get("/api/contas/99"))
                .andExpect(status().isNotFound());
    }
}
