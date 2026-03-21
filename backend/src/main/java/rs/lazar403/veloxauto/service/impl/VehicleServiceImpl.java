package rs.lazar403.veloxauto.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.lazar403.veloxauto.dto.vehicle.VehicleCreateRequest;
import rs.lazar403.veloxauto.dto.vehicle.VehicleResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleUpdateRequest;
import rs.lazar403.veloxauto.mapper.VehicleMapper;
import rs.lazar403.veloxauto.model.Customer;
import rs.lazar403.veloxauto.model.Vehicle;
import rs.lazar403.veloxauto.repository.CustomerRepository;
import rs.lazar403.veloxauto.repository.VehicleRepository;
import rs.lazar403.veloxauto.service.VehicleService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    public VehicleResponse createVehicle(VehicleCreateRequest request) {
        if (vehicleRepository.existsByVin(request.getVin())) {
            throw new RuntimeException("VIN already exists.");
        }

        Customer creator = customerRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new RuntimeException("Creator not found with id: " + request.getCreatedById()));

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setCreatedBy(creator);
        vehicle.setIsActive(true);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        return vehicleMapper.toResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        return vehicleMapper.toResponseList(vehicleRepository.findAll());
    }

    @Override
    public VehicleResponse updateVehicle(Long id, VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        vehicleMapper.updateEntity(request, vehicle);
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(updatedVehicle);
    }

    @Override
    public void deactivateVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
    }
}
