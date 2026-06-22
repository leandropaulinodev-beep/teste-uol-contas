package com.uol.bancodigital.service;

import com.uol.bancodigital.domain.Notification;
import com.uol.bancodigital.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Async
    public void enviar(Long contaId, String mensagem) {
        var notificacao = new Notification(contaId, mensagem);
        notificationRepository.save(notificacao);
        log.info("Notificação enviada para conta {}: {}", contaId, mensagem);
    }
}
