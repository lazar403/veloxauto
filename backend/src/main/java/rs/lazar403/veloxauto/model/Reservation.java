package rs.lazar403.veloxauto.model;

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
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rs.lazar403.veloxauto.enums.ReservationStatus;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(
        name = "reservations",
        indexes = {
                @Index(name = "idx_reservation_customer", columnList = "customer_id"),
                @Index(name = "idx_reservation_vehicle", columnList = "vehicle_id"),
                @Index(name = "idx_reservation_status", columnList = "status"),
                @Index(name = "idx_reservation_expires", columnList = "expires_at")
        }
)
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [======== RELATIONSHIPS ========]
    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Vehicle is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // [======== RESERVATION DETAILS ========]
    @NotNull(message = "Expiration date is required")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(length = 500)
    private String notes;

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
    public boolean isActive() {
        if (expiresAt == null || status != ReservationStatus.ACTIVE) {
            return false;
        }
        return LocalDateTime.now().isBefore(expiresAt);
    }

    public boolean needsExpiration() {
        if (expiresAt == null || status != ReservationStatus.ACTIVE) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
