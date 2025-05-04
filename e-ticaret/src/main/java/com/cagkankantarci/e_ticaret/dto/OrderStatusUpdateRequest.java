package com.cagkankantarci.e_ticaret.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    
    @NotBlank(message = "Sipariş durumu boş olamaz")
    private String status;
}