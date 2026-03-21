package rs.lazar403.veloxauto.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rs.lazar403.veloxauto.dto.vehicle.VehicleCreateRequest;
import rs.lazar403.veloxauto.dto.vehicle.VehicleResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleUpdateRequest;
import rs.lazar403.veloxauto.enums.VehicleStatus;
import rs.lazar403.veloxauto.service.VehicleService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Slice test — only loads VehicleController, service is mocked
@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService vehicleService;

    // [======== CREATE ========]

    @Test
    void createVehicle_shouldReturn201WithBody() throws Exception {
        // arrange: build a valid request with all required fields
        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setCreatedById(1L);
        request.setMake("BMW");
        request.setModel("M3");
        request.setYear(2024);
        request.setVin("WBAPH5C55BA123456");
        request.setPrice(new BigDecimal("55000.00"));
        request.setStatus(VehicleStatus.AVAILABLE);
        request.setExchange(false);

        VehicleResponse response = new VehicleResponse();
        response.setId(1L);
        response.setMake("BMW");
        response.setModel("M3");

        when(vehicleService.createVehicle(any(VehicleCreateRequest.class))).thenReturn(response);

        // assert: POST returns 201 with the response body
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.make").value("BMW"));
    }

    @Test
    void createVehicle_withMissingRequiredFields_shouldReturn400() throws Exception {
        // arrange: empty request — make, model, year, vin, price, status, exchange are all required
        VehicleCreateRequest request = new VehicleCreateRequest();

        // assert: @Valid catches missing @NotNull/@NotBlank fields
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createVehicle_withInvalidPrice_shouldReturn400() throws Exception {
        // arrange: price is zero — @DecimalMin(exclusive) should reject it
        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setCreatedById(1L);
        request.setMake("BMW");
        request.setModel("M3");
        request.setYear(2024);
        request.setVin("WBAPH5C55BA123456");
        request.setPrice(BigDecimal.ZERO);
        request.setStatus(VehicleStatus.AVAILABLE);
        request.setExchange(false);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // [======== GET BY ID ========]

    @Test
    void getVehicleById_shouldReturn200() throws Exception {
        // arrange: service finds the vehicle
        VehicleResponse response = new VehicleResponse();
        response.setId(1L);
        response.setMake("BMW");

        when(vehicleService.getVehicleById(1L)).thenReturn(response);

        // assert
        mockMvc.perform(get("/api/vehicles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.make").value("BMW"));
    }

    // [======== GET ALL ========]

    @Test
    void getAllVehicles_shouldReturn200WithList() throws Exception {
        // arrange: service returns two vehicles
        VehicleResponse r1 = new VehicleResponse();
        r1.setId(1L);
        VehicleResponse r2 = new VehicleResponse();
        r2.setId(2L);

        when(vehicleService.getAllVehicles()).thenReturn(List.of(r1, r2));

        // assert: GET /api/vehicles returns array of size 2
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // [======== UPDATE ========]

    @Test
    void updateVehicle_shouldReturn200() throws Exception {
        // arrange: partial update — only changing color
        VehicleUpdateRequest request = new VehicleUpdateRequest();
        request.setColor("Red");

        VehicleResponse response = new VehicleResponse();
        response.setId(1L);
        response.setColor("Red");

        when(vehicleService.updateVehicle(eq(1L), any(VehicleUpdateRequest.class))).thenReturn(response);

        // assert
        mockMvc.perform(put("/api/vehicles/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Red"));
    }

    // [======== DEACTIVATE ========]

    @Test
    void deactivateVehicle_shouldReturn204() throws Exception {
        // arrange: service deactivates without returning a body
        doNothing().when(vehicleService).deactivateVehicle(1L);

        // assert: DELETE returns 204 No Content
        mockMvc.perform(delete("/api/vehicles/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
