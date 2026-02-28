package rs.lazar403.veloxauto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.lazar403.veloxauto.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByVin(String vin);
}
