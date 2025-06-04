package com.f1.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "F1 Dashboard API is running!";
    }
}