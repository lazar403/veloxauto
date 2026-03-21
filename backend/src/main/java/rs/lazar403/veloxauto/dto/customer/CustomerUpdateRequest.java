package rs.lazar403.veloxauto.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CustomerUpdateRequest {

    // [======== NAME ========]
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    // [======== EMAIL ========]
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    // [======== PHONE NUMBER ========]
    @Pattern(
            regexp = "^(\\+381|381|0)6[0-9]{7,8}$",
            message = "Phone number must be valid")
    private String phoneNumber;

    // [======== ADDITIONAL INFO ========]
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
}
