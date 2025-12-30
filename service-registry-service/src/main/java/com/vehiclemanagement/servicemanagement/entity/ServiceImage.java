package com.vehiclemanagement.servicemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequest serviceRequest;
    
    @Column(name = "image_name", length = 255)
    private String imageName;
    
    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData; // Store image as bytes
    
    @Column(name = "image_type", length = 50)
    private String imageType; // JPEG, PNG, etc.
}