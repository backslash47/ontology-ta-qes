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
