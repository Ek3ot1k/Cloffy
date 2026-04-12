package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Block;
import ru.amin.Rest.entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Integer> {

    Optional<Block> findByBlockerAndBlocked(Users blocker, Users blocked);

    // Список заблокированных текущим пользователем
    List<Block> findByBlocker(Users blocker);

    // Проверяем блокировку в любом направлении (A→B или B→A)
    @Query("SELECT COUNT(b) > 0 FROM Block b WHERE (b.blocker = :a AND b.blocked = :b) OR (b.blocker = :b AND b.blocked = :a)")
    boolean isBlockedInAnyDirection(@Param("a") Users a, @Param("b") Users b);
}
