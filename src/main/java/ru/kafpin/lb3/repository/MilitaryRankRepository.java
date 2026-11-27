package ru.kafpin.lb3.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kafpin.lb3.model.MilitaryRank;

public interface MilitaryRankRepository extends JpaRepository<MilitaryRank, Long> {
    boolean existsByNameIgnoreCase(String name);
}

