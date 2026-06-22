package com.uol.bancodigital.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(name = "enviado_em", nullable = false)
    private LocalDateTime enviadoEm;

    public Notification(Long contaId, String mensagem) {
        this.contaId = contaId;
        this.mensagem = mensagem;
        this.enviadoEm = LocalDateTime.now();
    }
}
