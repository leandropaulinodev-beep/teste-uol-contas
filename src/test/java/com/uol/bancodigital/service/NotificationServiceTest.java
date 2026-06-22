package com.uol.bancodigital.service;

import com.uol.bancodigital.domain.Notification;
import com.uol.bancodigital.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void enviar_devePersistirNotificacao() {
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(i -> i.getArgument(0));

        notificationService.enviar(1L, "Teste de notificação");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification salva = captor.getValue();
        assertEquals(1L, salva.getContaId());
        assertEquals("Teste de notificação", salva.getMensagem());
        assertNotNull(salva.getEnviadoEm());
    }
}
