package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.server.item.entity.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("""
            select i from Item i
            where i.available = true
              and (
                    lower(i.name) like lower(concat('%', :text, '%'))
                 or lower(i.description) like lower(concat('%', :text, '%'))
              )
            order by i.id asc
            """)
    List<Item> search(@Param("text") String text);

    @Query("""
        select i from Item i
        join fetch i.owner
        where i.request.id = :requestId
        order by i.id asc
        """)
    List<Item> findAllByRequestId(@Param("requestId") Long requestId);

    @Query("""
        select i from Item i
        join fetch i.owner
        where i.request.id in :requestIds
        order by i.id asc
        """)
    List<Item> findAllByRequestIdIn(@Param("requestIds") Collection<Long> requestIds);
}
