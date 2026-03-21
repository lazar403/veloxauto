package rs.lazar403.veloxauto.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.lazar403.veloxauto.enums.FuelType;
import rs.lazar403.veloxauto.enums.TransmissionType;
import rs.lazar403.veloxauto.enums.VehicleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class VehicleResponse {

    private Long id;
    private Long createdById;

    // [======== BASIC INFO ========]
    private String make;
    private String model;
    private Integer year;
    private String vin;

    // [======== PRICING ========]
    private BigDecimal price;

    // [======== VEHICLE SPECS ========]
    private Integer mileage;
    private String color;
    private TransmissionType transmission;
    private FuelType fuelType;
    private Integer engineCapacity;
    private String description;

    // [======== STATUS ========]
    private VehicleStatus status;
    private Boolean isActive;
    private Boolean exchange;

    // [======== AUDIT ========]
    private LocalDateTime createdAt;
}
