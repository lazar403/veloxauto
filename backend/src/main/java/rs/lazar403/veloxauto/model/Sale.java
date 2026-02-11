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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rs.lazar403.veloxauto.enums.SalePaymentMethod;
import rs.lazar403.veloxauto.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(
        name = "sales",
        indexes = {
                @Index(name = "idx_sale_customer", columnList = "customer_id"),
                @Index(name = "idx_sale_vehicle", columnList = "vehicle_id"),
                @Index(name = "idx_sale_salesperson", columnList = "salesperson_id"),
                @Index(name = "idx_sale_status", columnList = "status"),
                @Index(name = "idx_sale_completed_at", columnList = "completed_at")
        }
)
public class Sale {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [======== RELATIONSHIPS ========]
    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Vehicle is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, unique = true)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesperson_id")
    private Customer salesPerson;

    // [======== PRICING ========]
    @NotNull(message = "Final price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Final price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Final price must be a valid monetary amount")
    @Column(name = "final_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalPrice;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Discount must be a valid monetary amount")
    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    // [======== PAYMENT ========]
    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private SalePaymentMethod paymentMethod;

    // [======== STATUS ========]
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SaleStatus status = SaleStatus.PENDING;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // [======== NOTES ========]
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(length = 1000)
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
    public void complete() {
        this.status = SaleStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = SaleStatus.CANCELED;
    }

    public BigDecimal getOriginalPrice() {
        return finalPrice.add(discount != null ? discount : BigDecimal.ZERO);
    }
}
