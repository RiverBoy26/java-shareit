package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.item.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        select c from Comment c
        join fetch c.author
        where c.item.id = :itemId
        order by c.created asc
        """)
    List<Comment> findAllByItemIdWithAuthor(@Param("itemId") Long itemId);
}
