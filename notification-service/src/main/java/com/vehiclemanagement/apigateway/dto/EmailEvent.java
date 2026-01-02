package com.vehiclemanagement.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String to;
    private String subject;
    private String customerName;
    private String billNumber;
    private String vehicleInfo;
    private Double laborCost;
    private Double partsCost;
    private Double tax;
    private Double totalAmount;
    private List<Map<String, Object>> partsUsed;
    private String qrCodeBase64;
    private String qrCodeCid;
}
