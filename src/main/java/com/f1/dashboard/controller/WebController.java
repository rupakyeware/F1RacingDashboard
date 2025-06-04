package com.f1.dashboard.controller;

import com.f1.dashboard.model.Driver;
import com.f1.dashboard.model.Meeting;
import com.f1.dashboard.service.F1DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebController {

    private final F1DataService f1DataService;

    @Autowired
    public WebController(F1DataService f1DataService) {
        this.f1DataService = f1DataService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Meeting> meetings = f1DataService.getLastFiveRaces();
        model.addAttribute("meetings", meetings);
        return "dashboard";
    }
}