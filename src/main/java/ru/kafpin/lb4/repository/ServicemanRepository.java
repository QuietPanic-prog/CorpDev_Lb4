package ru.kafpin.lb4.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.kafpin.lb4.mapper.ServicemanMapper;
import ru.kafpin.lb4.model.Serviceman;

@Repository
public class ServicemanRepository {
    private final JdbcTemplate jdbcTemplate;

    public ServicemanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Serviceman> findAll() {
        String sql = """
                select s.*, r.name as rank_name
                from serviceman s
                left join military_rank r on r.id = s.rank_id
                order by s.id
                """;
        return jdbcTemplate.query(sql, new ServicemanMapper());
    }

    public Optional<Serviceman> findById(Long id) {
        String sql = """
                select s.*, r.name as rank_name
                from serviceman s
                left join military_rank r on r.id = s.rank_id
                where s.id = ?
                """;
        List<Serviceman> list = jdbcTemplate.query(sql, new ServicemanMapper(), id);
        return list.stream().findFirst();
    }

    public List<Serviceman> findByRankName(String rankName) {
        String sql = """
                select s.*, r.name as rank_name
                from serviceman s
                left join military_rank r on r.id = s.rank_id
                where r.name = ?
                order by s.id
                """;
        return jdbcTemplate.query(sql, new ServicemanMapper(), rankName);
    }

    public boolean existsByRankId(Long rankId) {
        Long count = jdbcTemplate.queryForObject(
                "select count(*) from serviceman where rank_id = ?", Long.class, rankId);
        return count != null && count > 0;
    }

    public Long save(Serviceman serviceman) {
        String sql = """
                insert into serviceman(last_name, first_name, patronymic, nationality,
                    birth_date, position, postal_code, country, region, district,
                    city, street, house, apartment, rank_id)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, serviceman.getLastName());
            ps.setString(2, serviceman.getFirstName());
            ps.setString(3, serviceman.getPatronymic());
            ps.setString(4, serviceman.getNationality());
            ps.setDate(5, Date.valueOf(serviceman.getBirthDate()));
            ps.setString(6, serviceman.getPosition());
            ps.setString(7, serviceman.getPostalCode());
            ps.setString(8, serviceman.getCountry());
            ps.setString(9, serviceman.getRegion());
            ps.setString(10, serviceman.getDistrict());
            ps.setString(11, serviceman.getCity());
            ps.setString(12, serviceman.getStreet());
            ps.setString(13, serviceman.getHouse());
            ps.setString(14, serviceman.getApartment());
            ps.setLong(15, serviceman.getRank().getId());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Не удалось получить сгенерированный ключ");
        }
        return key.longValue();
    }

    public int update(Serviceman serviceman) {
        String sql = """
                update serviceman set last_name = ?, first_name = ?, patronymic = ?,
                    nationality = ?, birth_date = ?, position = ?, postal_code = ?,
                    country = ?, region = ?, district = ?, city = ?, street = ?,
                    house = ?, apartment = ?, rank_id = ?
                where id = ?
                """;
        return jdbcTemplate.update(sql, serviceman.getLastName(), serviceman.getFirstName(),
                serviceman.getPatronymic(), serviceman.getNationality(),
                Date.valueOf(serviceman.getBirthDate()), serviceman.getPosition(),
                serviceman.getPostalCode(), serviceman.getCountry(), serviceman.getRegion(),
                serviceman.getDistrict(), serviceman.getCity(), serviceman.getStreet(),
                serviceman.getHouse(), serviceman.getApartment(), serviceman.getRank().getId(),
                serviceman.getId());
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("delete from serviceman where id = ?", id);
    }
}
