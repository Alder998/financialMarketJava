package com.example.financialMarketJava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIEndPoint {

    @Autowired
    private APIController controller;

    @GetMapping("/api/status")
    public String displayStatus(@RequestParam String status) throws Exception {
        return controller.displayStatus(status);
    }
    
}