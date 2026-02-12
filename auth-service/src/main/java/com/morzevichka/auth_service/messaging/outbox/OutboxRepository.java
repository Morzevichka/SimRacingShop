package com.morzevichka.auth_service.messaging.outbox;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("select e from OutboxEvent e where e.status in :statuses order by e.createdAt asc")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<OutboxEvent> findUnprocessedEvents(
            @Param("statuses") List<OutboxEventStatus> statuses,
            Pageable page
    );
}
