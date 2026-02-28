package rs.lazar403.veloxauto.dto.vehicle;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.lazar403.veloxauto.enums.FuelType;
import rs.lazar403.veloxauto.enums.TransmissionType;
import rs.lazar403.veloxauto.enums.VehicleStatus;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class VehicleUpdateRequest {

    // [======== BASIC INFO ========]
    @Size(max = 50, message = "Make must not exceed 50 characters")
    private String make;

    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @Min(value = 1886, message = "Year must be greater than or equal to 1886")
    @Max(value = 2100, message = "Year must be less than or equal to 2100")
    private Integer year;

    // [======== PRICING ========]
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary amount")
    private BigDecimal price;

    // [======== VEHICLE SPECS ========]
    @Min(value = 0, message = "Mileage cannot be negative")
    private Integer mileage;

    @Size(max = 30, message = "Color must not exceed 30 characters")
    private String color;

    private TransmissionType transmission;

    private FuelType fuelType;

    @Min(value = 0, message = "Engine capacity cannot be negative")
    private Integer engineCapacity;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    // [======== STATUS ========]
    private VehicleStatus status;

    private Boolean exchange;
}
