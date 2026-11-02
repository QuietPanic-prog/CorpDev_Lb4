package ru.kafpin.lb4.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.kafpin.lb4.model.MilitaryRank;

public class MilitaryRankMapper implements RowMapper<MilitaryRank> {

    @Override
    public MilitaryRank mapRow(ResultSet rs, int rowNum) throws SQLException {
        MilitaryRank rank = new MilitaryRank();
        rank.setId(rs.getLong("id"));
        rank.setName(rs.getString("name"));
        return rank;
    }
}
