package com.example.demo.controller;

import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import com.example.demo.service.IPurchaseOrderOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PurchaseOrderController {
    private final IPurchaseOrderOrchestratorService purchaseOrderOrchestratorService;

    public PurchaseOrderController(IPurchaseOrderOrchestratorService purchaseOrderOrchestratorService) {
        this.purchaseOrderOrchestratorService = purchaseOrderOrchestratorService;
    }

    @PostMapping("/order")
    public @ResponseBody ResponseEntity<ResponseOrderDto> createNewOrder(@RequestBody RequestOrderDto requestOrderDto) throws Exception {
        return ResponseEntity.ok(purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto));
    }
}
