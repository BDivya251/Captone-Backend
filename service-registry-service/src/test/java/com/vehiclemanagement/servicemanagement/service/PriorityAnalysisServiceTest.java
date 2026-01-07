package com.vehiclemanagement.servicemanagement.service;

import com.google.genai.Client;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito. junit.jupiter.MockitoExtension;
import org.springframework. test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PriorityAnalysisServiceTest {

    @Mock
    private Client client;

    private PriorityAnalysisService priorityAnalysisService;

    @BeforeEach
    void setUp() {
        priorityAnalysisService = new PriorityAnalysisService(client);
        ReflectionTestUtils.setField(priorityAnalysisService, "apiEnabled", false);
    }

    @Test
    void analyzePriority_BrakeKeyword_ReturnsHigh() {
        var response = priorityAnalysisService.analyzePriority("Brake pedal soft", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
        assertTrue(response.getReason().contains("Safety-critical"));
    }

    @Test
    void analyzePriority_EngineKeyword_ReturnsHigh() {
        var response = priorityAnalysisService. analyzePriority("Engine won't start", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
        assertTrue(response.getReason().contains("Safety-critical"));
    }

    @Test
    void analyzePriority_SmokeKeyword_ReturnsHigh() {
        var response = priorityAnalysisService.analyzePriority("Smoke from hood", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
    }

    @Test
    void analyzePriority_FireKeyword_ReturnsHigh() {
        var response = priorityAnalysisService.analyzePriority("Fire in engine", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
    }

    @Test
    void analyzePriority_AccidentKeyword_ReturnsHigh() {
        var response = priorityAnalysisService. analyzePriority("Accident damage", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
    }

    @Test
    void analyzePriority_WontStartKeyword_ReturnsHigh() {
        var response = priorityAnalysisService.analyzePriority("Car won't start", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
    }

    @Test
    void analyzePriority_NotStartingKeyword_ReturnsHigh() {
        var response = priorityAnalysisService.analyzePriority("Vehicle not starting", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
    }

    @Test
    void analyzePriority_NoiseKeyword_ReturnsMedium() {
        var response = priorityAnalysisService.analyzePriority("Strange noise", "2026-01-07");
        
        assertEquals("MEDIUM", response.getPriority());
        assertTrue(response.getReason().contains("Routine maintenance"));
    }

    @Test
    void analyzePriority_BatteryKeyword_ReturnsMedium() {
        var response = priorityAnalysisService.analyzePriority("Battery weak", "2026-01-07");
        
        assertEquals("MEDIUM", response.getPriority());
    }

    @Test
    void analyzePriority_ServiceKeyword_ReturnsMedium() {
        var response = priorityAnalysisService.analyzePriority("Regular service", "2026-01-07");
        
        assertEquals("MEDIUM", response.getPriority());
    }

    @Test
    void analyzePriority_OilKeyword_ReturnsMedium() {
        var response = priorityAnalysisService.analyzePriority("Oil change needed", "2026-01-07");
        
        assertEquals("MEDIUM", response.getPriority());
    }

    @Test
    void analyzePriority_VibrationKeyword_ReturnsMedium() {
        var response = priorityAnalysisService.analyzePriority("Steering vibration", "2026-01-07");
        
        assertEquals("MEDIUM", response.getPriority());
    }

    @Test
    void analyzePriority_MaintenanceKeyword_ReturnsMedium() {
        var response = priorityAnalysisService.analyzePriority("Scheduled maintenance", "2026-01-07");
        
        assertEquals("MEDIUM", response.getPriority());
    }
    @Test
    void analyzePriority_NoKeywords_ReturnsLow() {
        var response = priorityAnalysisService.analyzePriority("Minor scratch", "2026-01-07");
        
        assertEquals("LOW", response.getPriority());
        assertTrue(response.getReason().contains("Non-urgent"));
    }

    @Test
    void analyzePriority_CosmeticIssue_ReturnsLow() {
        var response = priorityAnalysisService.analyzePriority("Paint fading", "2026-01-07");
        
        assertEquals("LOW", response.getPriority());
    }

    @Test
    void analyzePriority_InteriorIssue_ReturnsLow() {
        var response = priorityAnalysisService.analyzePriority("Seat cover torn", "2026-01-07");
        
        assertEquals("LOW", response.getPriority());
    }
    @Test
    void analyzePriority_EmptyDescription_ReturnsLow() {
        var response = priorityAnalysisService.analyzePriority("", "2026-01-07");
        
        assertEquals("LOW", response.getPriority());
    }

    @Test
    void analyzePriority_UpperCase_Works() {
        var response = priorityAnalysisService.analyzePriority("BRAKE FAILURE", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
    }

    @Test
    void analyzePriority_MixedCase_Works() {
        var response = priorityAnalysisService. analyzePriority("EnGiNe SmOkE", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
    }

    @Test
    void analyzePriority_MultipleKeywords_HighPriority() {
        var response = priorityAnalysisService. analyzePriority("Brake noise oil leak", "2026-01-07");
        
        assertEquals("HIGH", response.getPriority());
    }
    @Test
    void responseDto_NoArgsConstructor() {
        var response = new PriorityAnalysisService. PriorityAnalysisResponse();
        
        assertNotNull(response);
        assertNull(response.getPriority());
        assertNull(response.getReason());
    }

    @Test
    void responseDto_AllArgsConstructor() {
        var response = new PriorityAnalysisService.PriorityAnalysisResponse("HIGH", "Critical");
        
        assertEquals("HIGH", response.getPriority());
        assertEquals("Critical", response.getReason());
    }

    @Test
    void responseDto_SettersGetters() {
        var response = new PriorityAnalysisService.PriorityAnalysisResponse();
        response.setPriority("MEDIUM");
        response.setReason("Test");
        
        assertEquals("MEDIUM", response.getPriority());
        assertEquals("Test", response.getReason());
    }
    @Test
    void analyzePriority_ApiDisabled_UsesFallback() {
        ReflectionTestUtils.setField(priorityAnalysisService, "apiEnabled", false);
        
        var response = priorityAnalysisService.analyzePriority("Brake issue", "2026-01-07");
        
        assertNotNull(response);
        assertEquals("HIGH", response.getPriority());
    }
    @Test
    void analyzePriority_GeminiEnabledButFails_FallsBack() {
        ReflectionTestUtils.setField(priorityAnalysisService, "apiEnabled", true);
        
        var response = priorityAnalysisService.analyzePriority("Brake failure", "2026-01-07");
        
        assertNotNull(response);
        assertEquals("HIGH", response.getPriority());
    }
}