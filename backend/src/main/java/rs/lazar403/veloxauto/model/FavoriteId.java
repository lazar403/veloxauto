package rs.lazar403.veloxauto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode
public class FavoriteId implements Serializable {

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "vehicle_id")
    private Long vehicleId;
}
