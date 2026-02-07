package rs.lazar403.veloxauto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rs.lazar403.veloxauto.enums.CustomerRole;

import java.time.LocalDateTime;

/*
    TODO:
        - Add CustomerRole
        - Add audit fields (updatedAt)
        - Add FK and Relations
 */
@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(
        name = "customers",
        indexes = {
                @Index(name = "idx_customer_email", columnList = "email")
        }
)
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [======== NAME ========]
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name")
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
    @Column(name = "phone_number")
    private String phoneNumber;

    // [======== BIZ STATUS ========]
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerRole role = CustomerRole.CUSTOMER;

    // [======== AUDIT LOGOVI ========]
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
