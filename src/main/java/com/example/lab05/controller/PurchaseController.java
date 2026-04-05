package com.example.lab05.controller;

import com.example.lab05.dto.PurchaseRequest;
import com.example.lab05.model.mongo.PurchaseReceipt;
import com.example.lab05.repository.PurchaseReceiptRepository;
import com.example.lab05.service.PurchaseService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/58-16088/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseReceiptRepository receiptRepository;

    public PurchaseController(PurchaseService purchaseService, PurchaseReceiptRepository receiptRepository) {
        this.purchaseService = purchaseService;
        this.receiptRepository = receiptRepository;
    }

    @PostMapping
    public PurchaseReceipt purchase(@RequestBody PurchaseRequest request) {
        return purchaseService.executePurchase(request);
    }

    @GetMapping("/person/{personName}")
    public List<PurchaseReceipt> getByPerson(@PathVariable String personName) {
        return receiptRepository.findByPersonName(personName);
    }
}
