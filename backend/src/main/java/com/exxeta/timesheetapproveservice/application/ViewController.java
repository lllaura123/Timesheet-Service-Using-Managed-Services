package com.exxeta.timesheetapproveservice.application;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    @RequestMapping({"/en/", "/en/input", "/en/timesheets", "/en/login"})
    public String indexEn() {
        return "forward:/en/index.html";
    }

    @RequestMapping({"/", "/timesheets", "/login", "/input"})
    public String indexDe() {
        return "forward:/index.html";
    }
}
