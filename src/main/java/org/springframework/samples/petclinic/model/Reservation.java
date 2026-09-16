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
package org.springframework.samples.petclinic.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Simple JavaBean domain object representing a reservation (a pet owner's advance booking for a future visit,
 * as opposed to a {@link Visit} which records a visit that already took place).
 *
 * @author Michael Isvy
 */
@Entity
@Table(name = "reservations")
public class Reservation extends BaseEntity {

    /**
     * Holds value of property reservationDate.
     */
    @Column(name = "reservation_date")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate reservationDate;

    /**
     * Holds value of property reservationTime.
     */
    @Column(name = "reservation_time")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime reservationTime;

    /**
     * Holds value of property reason.
     */
    @NotEmpty
    @Column(name = "reason")
    private String reason;

    /**
     * Holds value of property status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReservationStatus status;

    /**
     * Holds value of property pet.
     */
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    /**
     * Holds value of property vet. May be left unassigned until a member of staff confirms the reservation.
     */
    @ManyToOne
    @JoinColumn(name = "vet_id")
    private Vet vet;


    /**
     * Creates a new instance of Reservation for the current date, defaulting its status to REQUESTED.
     */
    public Reservation() {
        this.reservationDate = LocalDate.now();
        this.status = ReservationStatus.REQUESTED;
    }


    public LocalDate getReservationDate() {
        return this.reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getReservationTime() {
        return this.reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReservationStatus getStatus() {
        return this.status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Pet getPet() {
        return this.pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Vet getVet() {
        return this.vet;
    }

    public void setVet(Vet vet) {
        this.vet = vet;
    }

}
