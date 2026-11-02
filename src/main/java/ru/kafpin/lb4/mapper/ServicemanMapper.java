package ru.kafpin.lb4.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.kafpin.lb4.model.MilitaryRank;
import ru.kafpin.lb4.model.Serviceman;

public class ServicemanMapper implements RowMapper<Serviceman> {

    @Override
    public Serviceman mapRow(ResultSet rs, int rowNum) throws SQLException {
        Serviceman serviceman = new Serviceman();
        serviceman.setId(rs.getLong("id"));
        serviceman.setLastName(rs.getString("last_name"));
        serviceman.setFirstName(rs.getString("first_name"));
        serviceman.setPatronymic(rs.getString("patronymic"));
        serviceman.setNationality(rs.getString("nationality"));
        Date birthDate = rs.getDate("birth_date");
        serviceman.setBirthDate(birthDate == null ? null : birthDate.toLocalDate());
        serviceman.setPosition(rs.getString("position"));
        serviceman.setPostalCode(rs.getString("postal_code"));
        serviceman.setCountry(rs.getString("country"));
        serviceman.setRegion(rs.getString("region"));
        serviceman.setDistrict(rs.getString("district"));
        serviceman.setCity(rs.getString("city"));
        serviceman.setStreet(rs.getString("street"));
        serviceman.setHouse(rs.getString("house"));
        serviceman.setApartment(rs.getString("apartment"));

        long rankId = rs.getLong("rank_id");
        if (!rs.wasNull()) {
            MilitaryRank rank = new MilitaryRank();
            rank.setId(rankId);
            rank.setName(rs.getString("rank_name"));
            serviceman.setRank(rank);
        }
        return serviceman;
    }
}
