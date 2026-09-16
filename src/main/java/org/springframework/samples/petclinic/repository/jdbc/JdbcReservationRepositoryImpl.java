/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Reservation;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple JDBC-based implementation of the {@link ReservationRepository} interface.
 *
 * @author Michael Isvy
 */
@Repository
public class JdbcReservationRepositoryImpl implements ReservationRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;

    private JdbcTemplate plainJdbcTemplate;

    private SimpleJdbcInsert insertReservation;

    @Autowired
    public JdbcReservationRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.plainJdbcTemplate = new JdbcTemplate(dataSource);

        this.insertReservation = new SimpleJdbcInsert(dataSource)
            .withTableName("reservations")
            .usingGeneratedKeyColumns("id");
    }


    @Override
    public void save(Reservation reservation) {
        if (reservation.isNew()) {
            Number newKey = this.insertReservation.executeAndReturnKey(
                createReservationParameterSource(reservation));
            reservation.setId(newKey.intValue());
        } else {
            throw new UnsupportedOperationException("Reservation update not supported");
        }
    }


    /**
     * Creates a {@link MapSqlParameterSource} based on data values from the supplied {@link Reservation} instance.
     */
    private MapSqlParameterSource createReservationParameterSource(Reservation reservation) {
        return new MapSqlParameterSource()
            .addValue("id", reservation.getId())
            .addValue("reservation_date", reservation.getReservationDate())
            .addValue("reservation_time", reservation.getReservationTime())
            .addValue("reason", reservation.getReason())
            .addValue("status", reservation.getStatus() != null ? reservation.getStatus().name() : null)
            .addValue("pet_id", reservation.getPet().getId())
            .addValue("vet_id", reservation.getVet() != null ? reservation.getVet().getId() : null);
    }

    @Override
    public List<Reservation> findByPetId(Integer petId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", petId);
        JdbcPet pet = this.jdbcTemplate.queryForObject(
                "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE id=:id",
                params,
                new JdbcPetRowMapper());

        List<Reservation> reservations = this.jdbcTemplate.query(
            "SELECT id as reservation_id, reservation_date, reservation_time, reason, status, vet_id "
                + "FROM reservations WHERE pet_id=:id",
            params, reservationRowMapper());

        for (Reservation reservation : reservations) {
            reservation.setPet(pet);
        }

        return reservations;
    }

    @Override
    public List<Reservation> findAll() {
        return this.plainJdbcTemplate.query(
            "SELECT r.id as reservation_id, r.reservation_date, r.reservation_time, r.reason, r.status, "
                + "r.vet_id, r.pet_id FROM reservations r ORDER BY r.reservation_date, r.reservation_time",
            (rs, rowNum) -> {
                Reservation reservation = reservationRowMapper().mapRow(rs, rowNum);
                int petId = rs.getInt("pet_id");
                Pet pet = this.plainJdbcTemplate.queryForObject(
                    "SELECT id, name, birth_date, type_id, owner_id FROM pets WHERE id=?",
                    new JdbcPetRowMapper(), petId);
                reservation.setPet(pet);
                return reservation;
            });
    }

    /**
     * Builds a {@link RowMapper} that maps the base reservation columns plus an optional {@code vet_id}
     * column into a fully populated {@link Vet} association.
     */
    private RowMapper<Reservation> reservationRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            Reservation reservation = new JdbcReservationRowMapper().mapRow(rs, rowNum);
            int vetId = rs.getInt("vet_id");
            if (!rs.wasNull()) {
                Vet vet = this.plainJdbcTemplate.queryForObject(
                    "SELECT id, first_name, last_name FROM vets WHERE id=?",
                    BeanPropertyRowMapper.newInstance(Vet.class), vetId);
                reservation.setVet(vet);
            }
            return reservation;
        };
    }

}
