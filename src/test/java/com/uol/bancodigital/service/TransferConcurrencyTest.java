package com.uol.bancodigital.service;

import com.uol.bancodigital.domain.Account;
import com.uol.bancodigital.dto.TransferRequest;
import com.uol.bancodigital.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração que simula alta concorrência.
 * 10 threads tentam transferir R$100 simultaneamente da mesma conta.
 * A conta origem tem R$1000, então todas as 10 devem ter sucesso
 * e o saldo total do sistema deve permanecer inalterado.
 */
@SpringBootTest
class TransferConcurrencyTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void transferenciasSimultaneas_devemManterConsistencia() throws InterruptedException {
        // Cria contas de teste
        Account origem = new Account("Conta Origem", new BigDecimal("1000.00"));
        Account destino = new Account("Conta Destino", new BigDecimal("0.00"));
        origem = accountRepository.save(origem);
        destino = accountRepository.save(destino);

        BigDecimal saldoTotalInicial = origem.getSaldo().add(destino.getSaldo());

        int numThreads = 10;
        BigDecimal valorPorTransferencia = new BigDecimal("100.00");

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);

        Long origemId = origem.getId();
        Long destinoId = destino.getId();

        // Todas as threads iniciam ao mesmo tempo
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    TransferRequest request = new TransferRequest(origemId, destinoId, valorPorTransferencia);
                    transferService.transferir(request);
                    sucessos.incrementAndGet();
                } catch (Exception e) {
                    falhas.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Recarrega as contas do banco
        Account origemAtualizada = accountRepository.findById(origemId).orElseThrow();
        Account destinoAtualizada = accountRepository.findById(destinoId).orElseThrow();

        BigDecimal saldoTotalFinal = origemAtualizada.getSaldo().add(destinoAtualizada.getSaldo());

        // Critério 1: saldo total do sistema não mudou (consistência)
        assertEquals(0, saldoTotalInicial.compareTo(saldoTotalFinal),
                "Saldo total do sistema deve permanecer inalterado: " +
                        saldoTotalInicial + " vs " + saldoTotalFinal);

        // Critério 2: saldo da origem nunca ficou negativo
        assertTrue(origemAtualizada.getSaldo().compareTo(BigDecimal.ZERO) >= 0,
                "Saldo da origem não pode ser negativo: " + origemAtualizada.getSaldo());

        // Critério 3: todas as 10 transferências devem ter passado (1000 / 100 = 10)
        assertEquals(10, sucessos.get(), "Todas as 10 transferências de R$100 devem ter sucesso");
        assertEquals(0, falhas.get(), "Nenhuma falha esperada com saldo suficiente");

        // Critério 4: saldos finais corretos
        assertEquals(0, origemAtualizada.getSaldo().compareTo(BigDecimal.ZERO),
                "Saldo da origem deve ser zero");
        assertEquals(0, destinoAtualizada.getSaldo().compareTo(new BigDecimal("1000.00")),
                "Saldo do destino deve ser 1000");
    }
}
