package com.example.demo.service.purchaseorder;

import com.example.demo.model.dto.external.RequestOrderDto;
import com.example.demo.model.dto.external.RequestProductDto;
import com.example.demo.model.dto.external.ResponseOrderDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderOrchestratorServiceTest {
    @InjectMocks
    private PurchaseOrderOrchestratorService purchaseOrderOrchestratorService;
    @Mock
    private StartOrderStep startOrderStep;


    @Test
    void issueNewOrderWithSteps() throws Exception {
        // arrange
        RequestOrderDto requestOrderDto = new RequestOrderDto(List.of(
                new RequestProductDto(11L, 3),
                new RequestProductDto(12L, 4),
                new RequestProductDto(13L, 2)
        ));
        when(startOrderStep.next()).thenReturn(Optional.empty());
        doNothing().when(startOrderStep).executeStepOperation(any());

        // act
        ResponseOrderDto responseOrderDto = purchaseOrderOrchestratorService.issueNewOrderWithSteps(requestOrderDto);

        // assert
        Assertions.assertNotNull(responseOrderDto);
    }
}