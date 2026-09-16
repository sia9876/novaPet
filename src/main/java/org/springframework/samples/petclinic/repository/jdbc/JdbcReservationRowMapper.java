/*
 * Copyright 2002-2015 the original author or authors.
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


import org.springframework.jdbc.core.RowMapper;
import org.springframework.samples.petclinic.model.Reservation;
import org.springframework.samples.petclinic.model.ReservationStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * {@link RowMapper} implementation mapping data from a {@link ResultSet} to the corresponding properties
 * of the {@link Reservation} class.
 */
class JdbcReservationRowMapper implements RowMapper<Reservation> {

    @Override
    public Reservation mapRow(ResultSet rs, int row) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("reservation_id"));
        reservation.setReservationDate(rs.getObject("reservation_date", LocalDate.class));
        reservation.setReservationTime(rs.getObject("reservation_time", LocalTime.class));
        reservation.setReason(rs.getString("reason"));
        String status = rs.getString("status");
        reservation.setStatus(status != null ? ReservationStatus.valueOf(status) : null);
        return reservation;
    }
}
