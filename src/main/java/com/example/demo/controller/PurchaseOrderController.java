package com.example.demo.controller;

import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import com.example.demo.service.purchaseorder.PurchaseOrderOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PurchaseOrderController {
    private final PurchaseOrderOrchestratorService purchaseOrderOrchestratorService;

    public PurchaseOrderController(PurchaseOrderOrchestratorService purchaseOrderOrchestratorService) {
        this.purchaseOrderOrchestratorService = purchaseOrderOrchestratorService;
    }

    @PostMapping("/order")
    public @ResponseBody ResponseEntity<ResponseOrderDto> createNewOrder(@RequestBody RequestOrderDto requestOrderDto) throws Exception {
        return ResponseEntity.ok(purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto));
    }
}
