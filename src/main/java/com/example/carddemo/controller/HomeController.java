package com.example.carddemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This file is just a stopgap to be replaced once all the remaining COBOL programs are migrated.
 * This for certain should be replaced with the modernized version of CC00 transaction (signon)
 */
@RestController
public class HomeController {

    @GetMapping(value = "/")
    public String home() {
        return """
            <h1>AWS Mainframe Modernization Card Demo Application</h1>
            <h3>Available Endpoints:</h3>
            <ul>
                <li><a href="/transactions">List all transactions</a></li>
            </ul>
            """;
    }
}