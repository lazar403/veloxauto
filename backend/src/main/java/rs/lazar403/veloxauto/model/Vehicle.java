package rs.lazar403.veloxauto.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rs.lazar403.veloxauto.enums.FuelType;
import rs.lazar403.veloxauto.enums.TransmissionType;
import rs.lazar403.veloxauto.enums.VehicleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(
        name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicle_make_model", columnList = "make, model"),
                @Index(name = "idx_vehicle_status", columnList = "status"),
                @Index(name = "idx_vehicle_price", columnList = "price"),
                @Index(name = "idx_vehicle_year", columnList = "year")
        }
)
public class Vehicle {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Creator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Customer createdBy;

    // [======== BASIC INFO ========]
    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1886, message = "Year must be greater than or equal to 1886")
    @Max(value = 2100, message = "Year must be less than or equal to 2100")
    @Column(nullable = false)
    private Integer year;

    @NotBlank(message = "VIN is required")
    @Size(min = 11, max = 17, message = "VIN must be between 11 and 17 characters")
    @Column(nullable = false, unique = true, length = 17)
    private String vin;

    // [======== PRICING ========]
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary amount")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    // [======== VEHICLE SPECS ========]
    @Min(value = 0, message = "Mileage cannot be negative")
    private Integer mileage;

    @Size(max = 30, message = "Color must not exceed 30 characters")
    @Column(length = 30)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransmissionType transmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", length = 20)
    private FuelType fuelType;

    @Min(value = 0, message = "Engine capacity cannot be negative")
    @Column(name = "engine_capacity")
    private Integer engineCapacity; // in cc

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(length = 2000)
    private String description;

    // [======== STATUS ========]
    @NotNull(message = "Vehicle status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status;

    @NotNull(message = "Active status must be specified")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull(message = "Exchange option must be specified")
    @Column(nullable = false)
    private Boolean exchange;

    // [======== RELATIONSHIPS ========]
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehicleImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle")
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    // [======== CONCURRENCY ========]
    @Version
    private Long version;

    // [======== AUDIT ========]
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // [======== HELPER METHODS ========]
    public void addImage(VehicleImage image) {
        images.add(image);
        image.setVehicle(this);
    }

    public void removeImage(VehicleImage image) {
        images.remove(image);
        image.setVehicle(null);
    }
}
