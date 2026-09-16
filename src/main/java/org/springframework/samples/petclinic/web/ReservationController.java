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
package org.springframework.samples.petclinic.web;

import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Reservation;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling advance reservations for a pet, as opposed to {@link VisitController} which records visits
 * that have already taken place.
 *
 * @author Michael Isvy
 */
@Controller
public class ReservationController {

    private final ClinicService clinicService;


    @Autowired
    public ReservationController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @ModelAttribute("vets")
    public Collection<Vet> populateVets() {
        return this.clinicService.findVets();
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    /**
     * Called before each and every @GetMapping or @PostMapping annotated method.
     * 2 goals:
     * - Make sure we always have fresh data
     * - Since we do not use the session scope, make sure that Pet object always has an id
     * (Even though id is not part of the form fields)
     *
     * @param petId
     * @return Reservation
     */
    @ModelAttribute("reservation")
    public Reservation loadPetWithReservation(@PathVariable("petId") int petId) {
        Pet pet = this.clinicService.findPetById(petId);
        Reservation reservation = new Reservation();
        pet.addReservation(reservation);
        return reservation;
    }

    // Spring MVC calls method loadPetWithReservation(...) before initNewReservationForm is called
    @GetMapping(value = "/owners/*/pets/{petId}/reservations/new")
    public String initNewReservationForm(@PathVariable("petId") int petId, Map<String, Object> model) {
        return "pets/createOrUpdateReservationForm";
    }

    // Spring MVC calls method loadPetWithReservation(...) before processNewReservationForm is called
    @PostMapping(value = "/owners/{ownerId}/pets/{petId}/reservations/new")
    public String processNewReservationForm(@Valid Reservation reservation, BindingResult result) {
        if (result.hasErrors()) {
            return "pets/createOrUpdateReservationForm";
        } else {
            this.clinicService.saveReservation(reservation);
            return "redirect:/owners/{ownerId}";
        }
    }

    @GetMapping(value = "/owners/*/pets/{petId}/reservations")
    public String showReservations(@PathVariable int petId, Map<String, Object> model) {
        model.put("reservations", this.clinicService.findPetById(petId).getReservations());
        return "reservationList";
    }

    @GetMapping(value = "/reservations")
    public String showAllReservations(Map<String, Object> model) {
        model.put("reservations", this.clinicService.findAllReservations());
        return "reservationList";
    }

}
