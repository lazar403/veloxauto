package rs.lazar403.veloxauto.service;

import rs.lazar403.veloxauto.dto.vehicle.VehicleCreateRequest;
import rs.lazar403.veloxauto.dto.vehicle.VehicleResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleUpdateRequest;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(VehicleCreateRequest request);

    VehicleResponse getVehicleById(Long id);

    List<VehicleResponse> getAllVehicles();

    VehicleResponse updateVehicle(Long id, VehicleUpdateRequest request);

    void deactivateVehicle(Long id);
}
