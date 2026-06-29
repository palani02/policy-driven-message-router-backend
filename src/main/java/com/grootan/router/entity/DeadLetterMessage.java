package com.grootan.router.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dead_letter_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeadLetterMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "message_delivery_id", nullable = false, unique = true)
    private MessageDelivery messageDelivery;

    @Column(nullable = false)
    private String reason;

    private LocalDateTime failedAt;
}