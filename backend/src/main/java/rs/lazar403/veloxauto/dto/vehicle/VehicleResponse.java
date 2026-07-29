package rs.lazar403.veloxauto.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.lazar403.veloxauto.enums.FuelType;
import rs.lazar403.veloxauto.enums.TransmissionType;
import rs.lazar403.veloxauto.enums.VehicleStatus;
import rs.lazar403.veloxauto.enums.CustomerRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class VehicleResponse {

    private Long id;
    private Long createdById;
    private String createdByName;
    private String createdByPhoneNumber;
    private CustomerRole createdByRole;

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
    private String primaryImageUrl;
    private List<String> imageUrls;

    // [======== AUDIT ========]
    private LocalDateTime createdAt;
}
