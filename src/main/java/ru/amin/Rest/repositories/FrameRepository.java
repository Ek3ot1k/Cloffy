package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Frame;

@Repository
public interface FrameRepository extends JpaRepository<Frame, Integer> {
}
