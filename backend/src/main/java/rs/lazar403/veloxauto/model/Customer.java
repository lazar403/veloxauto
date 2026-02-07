package rs.lazar403.veloxauto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Table(name = "customers")
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String username;

    @Column(unique = true)
    private String email;
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;
    private LocalDateTime createdAt;
}
