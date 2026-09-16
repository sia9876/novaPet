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
package org.springframework.samples.petclinic.repository.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.samples.petclinic.model.Reservation;
import org.springframework.samples.petclinic.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA implementation of the ClinicService interface using EntityManager.
 * <p/>
 * <p>The mappings are defined via annotations on the domain classes, discovered through
 * {@code packagesToScan}.
 *
 * @author Michael Isvy
 */
@Repository
public class JpaReservationRepositoryImpl implements ReservationRepository {

    @PersistenceContext
    private EntityManager em;


    @Override
    public void save(Reservation reservation) {
        if (reservation.getId() == null) {
            this.em.persist(reservation);
        } else {
            this.em.merge(reservation);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Reservation> findByPetId(Integer petId) {
        Query query = this.em.createQuery("SELECT r FROM Reservation r where r.pet.id= :id");
        query.setParameter("id", petId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Reservation> findAll() {
        Query query = this.em.createQuery("SELECT r FROM Reservation r ORDER BY r.reservationDate, r.reservationTime");
        return query.getResultList();
    }

}
