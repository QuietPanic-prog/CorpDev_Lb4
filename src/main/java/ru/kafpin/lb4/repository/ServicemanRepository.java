package ru.kafpin.lb4.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kafpin.lb4.model.Serviceman;

public interface ServicemanRepository extends JpaRepository<Serviceman, Long> {

    List<Serviceman> findByRankName(String rankName);

    boolean existsByRankId(Long rankId);
}
