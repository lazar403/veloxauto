package rs.lazar403.veloxauto.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.lazar403.veloxauto.enums.VehicleStatus;
import rs.lazar403.veloxauto.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByVin(String vin);

    Page<Vehicle> findByIsActive(boolean active, Pageable pageable);

    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);

    Page<Vehicle> findByIsActiveAndStatus(boolean active, VehicleStatus status, Pageable pageable);
}
