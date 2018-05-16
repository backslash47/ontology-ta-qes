/*
 * Copyright (C) 2018 Matus Zamborsky
 * This file is part of The ONT Detective.
 *
 * The ONT Detective is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ONT Detective is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with The ONT Detective.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.zamborsky.ontology.controllers;

import cz.zamborsky.ontology.services.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/generate")
@RestController
public class GeneratorController {
    @Autowired
    private PdfGenerator generator;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<byte[]> generate(@RequestBody String jwt) throws IOException {
        byte[] content = generator.generate(jwt);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=\"declaration.pdf\"");
        responseHeaders.add("Content-Type","application/pdf");

        return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
    }
}
