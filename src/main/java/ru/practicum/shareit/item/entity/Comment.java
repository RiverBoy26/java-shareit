package ru.practicum.shareit.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "created")
    private LocalDateTime createDate;
}
