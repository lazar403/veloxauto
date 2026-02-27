package rs.lazar403.veloxauto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rs.lazar403.veloxauto.enums.CustomerRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Table(
        name = "customers",
        indexes = {
                @Index(name = "idx_customer_email", columnList = "email"),
                @Index(name = "idx_customer_phone", columnList = "phone_number")
        }
)
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [======== NAME ========]
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", length = 50)
    private String lastName;

    // [======== EMAIL ========]
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    // [======== PASSWORD ========]
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(nullable = false, length = 100)
    private String password;

    // [======== PHONE NUMBER ========]
    @Pattern(
            regexp = "^(\\+381|381|0)6[0-9]{7,8}$",
            message = "Phone number must be valid (e.g. +381601234567 or 0601234567).")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // [======== ADDITIONAL INFO ========]
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(length = 255)
    private String address;

    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // [======== BIZ STATUS ========]
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerRole role = CustomerRole.CUSTOMER;

    // [======== RELATIONSHIPS ========]
    @OneToMany(mappedBy = "customer")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    @Builder.Default
    private List<Sale> purchases = new ArrayList<>();

    @OneToMany(mappedBy = "salesPerson")
    @Builder.Default
    private List<Sale> sales = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();

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
}
