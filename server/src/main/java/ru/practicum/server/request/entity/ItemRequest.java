package ru.practicum.server.request.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.server.user.entity.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", referencedColumnName = "id", nullable = false)
    private User requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
