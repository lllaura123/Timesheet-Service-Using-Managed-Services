package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.Language;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.ResourceBundle;

@CrossOrigin("http://localhost:4200")
@RestController
@Controller

public class LanguageController {

    @RequestMapping("lang")
    @PutMapping(value = "/{lang}")
    public ResponseEntity setLocale(@PathVariable String lang) {
        Language.locale = new Locale(lang);
        Language.bundle = ResourceBundle.getBundle(Language.bundleName, Language.locale);
        return ResponseEntity.ok().body(Language.bundle.getString("statusOk"));
    }

}
