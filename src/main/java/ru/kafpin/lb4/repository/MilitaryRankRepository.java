package ru.kafpin.lb4.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.kafpin.lb4.mapper.MilitaryRankMapper;
import ru.kafpin.lb4.model.MilitaryRank;

@Repository
public class MilitaryRankRepository {
    private final JdbcTemplate jdbcTemplate;

    public MilitaryRankRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MilitaryRank> findAll() {
        return jdbcTemplate.query("select * from military_rank order by id",
                new MilitaryRankMapper());
    }

    public Optional<MilitaryRank> findById(Long id) {
        List<MilitaryRank> list = jdbcTemplate.query(
                "select * from military_rank where id = ?", new MilitaryRankMapper(), id);
        return list.stream().findFirst();
    }

    public Optional<MilitaryRank> findByName(String name) {
        List<MilitaryRank> list = jdbcTemplate.query(
                "select * from military_rank where name = ?", new MilitaryRankMapper(), name);
        return list.stream().findFirst();
    }

    public boolean existsByName(String name) {
        Long count = jdbcTemplate.queryForObject(
                "select count(*) from military_rank where name = ?", Long.class, name);
        return count != null && count > 0;
    }

    public Long save(MilitaryRank rank) {
        String sql = "insert into military_rank(name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, rank.getName());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Не удалось получить сгенерированный ключ");
        }
        return key.longValue();
    }

    public int update(MilitaryRank rank) {
        return jdbcTemplate.update("update military_rank set name = ? where id = ?",
                rank.getName(), rank.getId());
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("delete from military_rank where id = ?", id);
    }
}
