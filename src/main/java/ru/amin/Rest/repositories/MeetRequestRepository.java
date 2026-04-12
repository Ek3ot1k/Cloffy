package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.MeetRequest;
import ru.amin.Rest.entity.Users;
import ru.amin.Rest.util.MeetStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetRequestRepository extends JpaRepository<MeetRequest, Integer> {

    // Активные встречи пользователя (входящие и исходящие)
    @Query("SELECT m FROM MeetRequest m WHERE (m.requester = :user OR m.receiver = :user) AND m.status = :status")
    List<MeetRequest> findByUserAndStatus(@Param("user") Users user, @Param("status") MeetStatus status);

    // Активный PENDING-запрос между двумя пользователями в любом направлении
    @Query("""
            SELECT m FROM MeetRequest m
            WHERE ((m.requester = :firstUser AND m.receiver = :secondUser)
               OR (m.requester = :secondUser AND m.receiver = :firstUser))
              AND m.status = :status
            """)
    Optional<MeetRequest> findActiveBetweenUsers(@Param("firstUser") Users firstUser,
                                                 @Param("secondUser") Users secondUser,
                                                 @Param("status") MeetStatus status);

    // Входящий PENDING-запрос от конкретного пользователя
    Optional<MeetRequest> findByRequesterAndReceiverAndStatus(Users requester, Users receiver, MeetStatus status);
}
