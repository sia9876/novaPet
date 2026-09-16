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


import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * Instructs Spring MVC on how to parse and print elements of type 'Vet', so that a &lt;select&gt; of vets (e.g. on
 * the reservation form) can bind directly to a {@link Vet} association.
 * <p/>
 * Also see how the bean 'conversionService' has been declared inside mvc-core-config.xml
 *
 * @author Michael Isvy
 */
public class VetFormatter implements Formatter<Vet> {

    private final ClinicService clinicService;


    @Autowired
    public VetFormatter(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Override
    public String print(Vet vet, Locale locale) {
        return vet != null ? String.valueOf(vet.getId()) : "";
    }

    @Override
    public Vet parse(String text, Locale locale) throws ParseException {
        if (text == null || text.isEmpty()) {
            return null;
        }
        Collection<Vet> findVets = this.clinicService.findVets();
        for (Vet vet : findVets) {
            if (String.valueOf(vet.getId()).equals(text)) {
                return vet;
            }
        }
        throw new ParseException("vet not found: " + text, 0);
    }

}
