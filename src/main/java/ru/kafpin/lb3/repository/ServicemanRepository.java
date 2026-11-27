package ru.kafpin.lb3.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kafpin.lb3.model.Serviceman;

public interface ServicemanRepository extends JpaRepository<Serviceman, Long> {
    @Query("""
        select s from Serviceman s
        join fetch s.address
        join fetch s.rank
        where (:lastName = '' or lower(s.lastName) like lower(concat('%', :lastName, '%')) escape '!')
          and (:nationality = '' or lower(s.nationality) = lower(:nationality))
          and (:city = '' or lower(s.address.city) like lower(concat('%', :city, '%')) escape '!')
          and (:position = '' or lower(s.position) like lower(concat('%', :position, '%')) escape '!')
          and (:rankId is null or s.rank.id = :rankId)
        order by s.lastName, s.firstName, s.id
        """)
    List<Serviceman> search(@Param("lastName") String lastName,
            @Param("nationality") String nationality, @Param("city") String city,
            @Param("position") String position, @Param("rankId") Long rankId);
}

