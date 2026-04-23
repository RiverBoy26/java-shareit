package ru.practicum.shareit.request.entity;

import jakarta.persistence.*;
import lombok.Getter;
import ru.practicum.shareit.user.entity.User;

@Getter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "requestor_id", referencedColumnName = "id", nullable = false)
    private User requestor;
}
