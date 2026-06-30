package ru.practicum.server.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.booking.entity.Booking;
import ru.practicum.server.booking.entity.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            where b.booker.id = :bookerId
            order by b.start desc
            """)
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            where b.booker.id = :bookerId
              and b.status = :status
            order by b.start desc
            """)
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            where b.booker.id = :bookerId
              and b.start < :now
              and b.end > :now
            order by b.start desc
            """)
    List<Booking> findAllCurrentByBookerId(Long bookerId, LocalDateTime now);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            where b.booker.id = :bookerId
              and b.end < :now
            order by b.start desc
            """)
    List<Booking> findAllPastByBookerId(Long bookerId, LocalDateTime now);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            where b.booker.id = :bookerId
              and b.start > :now
            order by b.start desc
            """)
    List<Booking> findAllFutureByBookerId(Long bookerId, LocalDateTime now);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            join fetch i.owner
            where i.owner.id = :ownerId
            order by b.start desc
            """)
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            join fetch i.owner
            where i.owner.id = :ownerId
              and b.status = :status
            order by b.start desc
            """)
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            join fetch i.owner
            where i.owner.id = :ownerId
              and b.start < :now
              and b.end > :now
            order by b.start desc
            """)
    List<Booking> findAllCurrentByOwnerId(Long ownerId, LocalDateTime now);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            join fetch i.owner
            where i.owner.id = :ownerId
              and b.end < :now
            order by b.start desc
            """)
    List<Booking> findAllPastByOwnerId(Long ownerId, LocalDateTime now);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch b.booker
            join fetch i.owner
            where i.owner.id = :ownerId
              and b.start > :now
            order by b.start desc
            """)
    List<Booking> findAllFutureByOwnerId(Long ownerId, LocalDateTime now);

    @Query("""
            select b from Booking b
            join fetch b.item i
            join fetch i.owner
            join fetch b.booker
            where b.id = :bookingId
            """)
    Optional<Booking> findByIdWithItemOwnerAndBooker(Long bookingId);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long userId,
            Long itemId,
            Status status,
            LocalDateTime end
    );

    Optional<Booking> findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
            Long itemId,
            Status status,
            LocalDateTime now
    );

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId,
            Status status,
            LocalDateTime now
    );
}
