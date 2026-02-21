package rs.lazar403.veloxauto.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "favorites",
        indexes = {
                @Index(name = "idx_favorite_customer", columnList = "customer_id"),
                @Index(name = "idx_favorite_vehicle", columnList = "vehicle_id")
        }
)
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    @NotNull(message = "Customer must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Vehicle must not be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vehicleId")
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // [======== FACTORY METHOD ========]
    /**
     * Creates a Favorite entity. Both customer and vehicle must be persisted (have non-null IDs).
     *
     * @throws IllegalArgumentException if customer/vehicle is null or not persisted
     */
    public static Favorite create(Customer customer, Vehicle vehicle) {
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Customer must be persisted before creating a favorite");
        }
        if (vehicle == null || vehicle.getId() == null) {
            throw new IllegalArgumentException("Vehicle must be persisted before creating a favorite");
        }
        Favorite favorite = new Favorite();
        favorite.setId(new FavoriteId(customer.getId(), vehicle.getId()));
        favorite.setCustomer(customer);
        favorite.setVehicle(vehicle);
        return favorite;
    }
}
