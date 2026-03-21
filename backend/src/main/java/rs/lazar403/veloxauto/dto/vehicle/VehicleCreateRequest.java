package rs.lazar403.veloxauto.dto.vehicle;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class VehicleCreateRequest {

    // [======== CREATOR ========]
    @NotNull(message = "Creator ID is required")
    private Long createdById;

    // [======== BASIC INFO ========]
    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make must not exceed 50 characters")
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1886, message = "Year must be greater than or equal to 1886")
    @Max(value = 2100, message = "Year must be less than or equal to 2100")
    private Integer year;

    @NotBlank(message = "VIN is required")
    @Size(min = 11, max = 17, message = "VIN must be between 11 and 17 characters")
    private String vin;

    // [======== PRICING ========]
    @NotNull(message = "Price is required")
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
    @NotNull(message = "Vehicle status is required")
    private VehicleStatus status;

    @NotNull(message = "Exchange option must be specified")
    private Boolean exchange;
}
