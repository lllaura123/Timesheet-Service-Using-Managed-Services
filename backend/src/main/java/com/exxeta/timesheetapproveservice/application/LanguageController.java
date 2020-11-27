package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.Language;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.ResourceBundle;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("lang")
public class LanguageController {

    /**
     * Set locale to the passed language and changes Resource Bundle
     *
     * @param lang Passed language
     * @return Responseentity.ok if Request succeeded
     */
    @PutMapping(value = "/{lang}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity setLocale(@PathVariable String lang) {
        Language.locale = new Locale(lang);
        Language.bundle = ResourceBundle.getBundle(Language.bundleName, Language.locale);
        System.out.println(Language.locale);
        return ResponseEntity.ok().body(Language.bundle.getString("statusOk"));
    }

}
