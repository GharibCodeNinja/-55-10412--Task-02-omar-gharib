package com.example.lab05.controller;

// ═══════════════════════════════════════════════════════════════
// TODO (Section 7 — Polyglot Integration):
// Uncomment this entire controller after implementing
// SmartProductPageService.
// ═══════════════════════════════════════════════════════════════

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab05.dto.SmartProductPage;
import com.example.lab05.service.SmartProductPageService;

@RestController
@RequestMapping("/api/smart")
public class SmartProductPageController {

    private final SmartProductPageService service;

    public SmartProductPageController(SmartProductPageService service) {
        this.service = service;
    }

    @GetMapping("/product/{id}")
    public SmartProductPage getPage(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Alice") String userName) {
        return service.getSmartPage(id, userName);
    }
}
