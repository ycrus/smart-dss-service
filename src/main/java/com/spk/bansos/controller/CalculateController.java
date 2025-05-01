package com.spk.bansos.controller;

import com.spk.bansos.service.CalculateServiceInterface;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "calculate")
@AllArgsConstructor
public class CalculateController {

    private final CalculateServiceInterface calculateServiceInterface;
    @GetMapping("/start/{id}")
    public String start(@PathVariable(value = "id") Long id ) {
        return calculateServiceInterface.startCalculate(id);
    }

    @GetMapping("/hello")
    public String testing() {
        return "Service is Running";
    }
}
