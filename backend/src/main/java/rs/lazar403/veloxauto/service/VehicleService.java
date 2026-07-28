package rs.lazar403.veloxauto.service;

import rs.lazar403.veloxauto.dto.common.PagedResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleCreateRequest;
import rs.lazar403.veloxauto.dto.vehicle.VehicleResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleUpdateRequest;
import rs.lazar403.veloxauto.enums.VehicleStatus;

public interface VehicleService {

    VehicleResponse createVehicle(VehicleCreateRequest request);

    VehicleResponse getVehicleById(Long id);

    PagedResponse<VehicleResponse> getVehicles(
            Boolean active,
            VehicleStatus status,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    VehicleResponse updateVehicle(Long id, VehicleUpdateRequest request);

    void deactivateVehicle(Long id);
}
