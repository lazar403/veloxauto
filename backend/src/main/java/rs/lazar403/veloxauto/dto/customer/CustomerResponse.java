package rs.lazar403.veloxauto.dto.customer;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.lazar403.veloxauto.enums.CustomerRole;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CustomerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean isActive;
    private CustomerRole role;
    private LocalDateTime createdAt;
}
