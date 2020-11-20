package com.exxeta.timesheetapproveservice.application;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    /**
     * Maps requests for all components and forwards call to english index.html
     *
     * @return View of the english version of the page
     */
    @RequestMapping({"/en/", "/en/input", "/en/timesheets", "/en/login"})
    public String indexEn() {
        return "forward:/en/index.html";
    }

    /**
     * Maps requests for all components and forwards calls to german index.html
     *
     * @return View of the german version of the page
     */
    @RequestMapping({"/", "/timesheets", "/login", "/input"})
    public String indexDe() {
        return "forward:/index.html";
    }
}
