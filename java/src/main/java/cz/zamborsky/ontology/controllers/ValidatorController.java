package cz.zamborsky.ontology.controllers;

import cz.zamborsky.ontology.services.PdfValidator;
import cz.zamborsky.ontology.services.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/validate")
@RestController
public class ValidatorController {
    @Autowired
    private PdfValidator validator;

    @RequestMapping(method = RequestMethod.POST)
    public Result validate(@RequestParam("file") MultipartFile file) throws IOException {
        return validator.validate(file.getBytes());
    }
}
