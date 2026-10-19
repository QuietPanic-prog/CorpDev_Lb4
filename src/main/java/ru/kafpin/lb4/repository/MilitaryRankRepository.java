package ru.kafpin.lb4.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kafpin.lb4.model.MilitaryRank;

public interface MilitaryRankRepository extends JpaRepository<MilitaryRank, Long> {

    Optional<MilitaryRank> findByName(String name);

    boolean existsByName(String name);
}
