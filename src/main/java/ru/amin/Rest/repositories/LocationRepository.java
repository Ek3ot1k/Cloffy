package ru.amin.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.amin.Rest.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location,Integer> {
}
